package dev.pfranccino.portfolio.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.pfranccino.portfolio.core.common.Either
import dev.pfranccino.portfolio.core.common.left
import dev.pfranccino.portfolio.core.common.right
import dev.pfranccino.portfolio.feature.settings.data.local.ThemePreferencesDataSource
import dev.pfranccino.portfolio.feature.settings.domain.SettingsError
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.nio.file.Path

/** Modo de noche del sistema falso: guarda lo que se le escribe. */
private class FakeNightModeSystem : AppNightModeSystem {
  val writes = mutableListOf<Int>()

  override fun write(nightMode: Int) {
    writes += nightMode
  }
}

/** Falla al escribir; la lectura devuelve preferencias vacías. */
private class FailingWriteDataStore : DataStore<Preferences> {
  override val data: Flow<Preferences> = flow { emit(emptyPreferences()) }

  override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
    throw IOException("sin espacio")
}

/** Falla las primeras [failures] lecturas con [IOException]; después lee del almacén real. */
private class ReadFailsDataStore(
  private var failures: Int,
  private val delegate: DataStore<Preferences>,
) : DataStore<Preferences> {

  override val data: Flow<Preferences> = flow {
    if (failures > 0) {
      failures--
      throw IOException("archivo ilegible")
    }
    emitAll(delegate.data)
  }

  override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
    delegate.updateData(transform)
}

class ThemeRepositoryImplTest {

  @TempDir
  lateinit var tempDir: Path

  private fun TestScope.newDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
      scope = backgroundScope,
      produceFile = { tempDir.resolve("settings.preferences_pb").toFile() },
    )

  private fun repositoryOver(
    store: DataStore<Preferences>,
    nightMode: FakeNightModeSystem = FakeNightModeSystem(),
  ): ThemeRepositoryImpl = ThemeRepositoryImpl(ThemePreferencesDataSource(store), nightMode)

  @Test
  fun `sin tema guardado, el modo es Sistema`() = runTest {
    val nightMode = FakeNightModeSystem()
    val repository = repositoryOver(newDataStore(), nightMode)

    assertEquals(ThemeMode.System, repository.observeThemeMode().first())
    assertEquals(emptyList<Int>(), nightMode.writes)
  }

  @Test
  fun `el tema guardado se lee después de escribirlo`() = runTest {
    val repository = repositoryOver(newDataStore())

    val saveDark: Either<SettingsError, Unit> = repository.setThemeMode(ThemeMode.Dark)
    assertEquals(Unit.right(), saveDark)
    assertEquals(ThemeMode.Dark, repository.observeThemeMode().first())

    repository.setThemeMode(ThemeMode.Light)
    assertEquals(ThemeMode.Light, repository.observeThemeMode().first())
  }

  @Test
  fun `guardar Oscuro fija el modo de noche de la app en sí`() = runTest {
    val nightMode = FakeNightModeSystem()
    val repository = repositoryOver(newDataStore(), nightMode)

    assertEquals(Unit.right(), repository.setThemeMode(ThemeMode.Dark))
    assertEquals(listOf(2), nightMode.writes)
  }

  @Test
  fun `guardar Claro fija el modo de noche de la app en no`() = runTest {
    val nightMode = FakeNightModeSystem()
    val repository = repositoryOver(newDataStore(), nightMode)

    assertEquals(Unit.right(), repository.setThemeMode(ThemeMode.Light))
    assertEquals(listOf(1), nightMode.writes)
  }

  @Test
  fun `guardar Sistema fija el modo de noche de la app en automático`() = runTest {
    val nightMode = FakeNightModeSystem()
    val repository = repositoryOver(newDataStore(), nightMode)

    assertEquals(Unit.right(), repository.setThemeMode(ThemeMode.System))
    assertEquals(listOf(0), nightMode.writes)
  }

  @Test
  fun `si guardar en DataStore falla, el sistema no cambia`() = runTest {
    val nightMode = FakeNightModeSystem()
    val repository = repositoryOver(FailingWriteDataStore(), nightMode)

    val result = repository.setThemeMode(ThemeMode.Dark)

    assertEquals(SettingsError.CouldNotSave.left(), result)
    assertEquals(emptyList<Int>(), nightMode.writes)
  }

  @Test
  fun `un valor desconocido en el almacén se lee como Sistema`() = runTest {
    val store = newDataStore()
    store.edit { preferences -> preferences[stringPreferencesKey("theme_mode")] = "morado" }

    assertEquals(ThemeMode.System, repositoryOver(store).observeThemeMode().first())
  }

  @Test
  fun `al leer un tema guardado se alinea el sistema una vez por arranque`() = runTest {
    val store = newDataStore()
    repositoryOver(store).setThemeMode(ThemeMode.Dark)
    // Simula una restauración: DataStore tiene Oscuro y el sistema no lo sabe.
    val nightMode = FakeNightModeSystem()
    val repository = repositoryOver(store, nightMode)

    assertEquals(ThemeMode.Dark, repository.observeThemeMode().first())
    assertEquals(listOf(2), nightMode.writes)

    // En el mismo arranque no vuelve a escribir.
    assertEquals(ThemeMode.Dark, repository.observeThemeMode().first())
    assertEquals(listOf(2), nightMode.writes)
  }

  @Test
  fun `un fallo al leer emite Sistema y luego el valor guardado si el reintento sale bien`() = runTest {
    val store = newDataStore()
    repositoryOver(store).setThemeMode(ThemeMode.Dark)
    val repository = repositoryOver(ReadFailsDataStore(failures = 1, delegate = store))

    val emitted = repository.observeThemeMode().take(2).toList()

    assertEquals(listOf(ThemeMode.System, ThemeMode.Dark), emitted)
  }

  @Test
  fun `tras tres reintentos fallidos se queda en Sistema y termina sin lanzar`() = runTest {
    val store = newDataStore()
    repositoryOver(store).setThemeMode(ThemeMode.Dark)
    val repository = repositoryOver(ReadFailsDataStore(failures = 10, delegate = store))

    // Un fallo inicial más tres reintentos: cuatro emisiones de Sistema, luego el flujo termina.
    val emitted = repository.observeThemeMode().toList()

    assertEquals(List(4) { ThemeMode.System }, emitted)
  }

  @Test
  fun `un fallo de lectura no cambia el modo de noche del sistema`() = runTest {
    val store = newDataStore()
    repositoryOver(store).setThemeMode(ThemeMode.Dark)
    val nightMode = FakeNightModeSystem()

    repositoryOver(ReadFailsDataStore(failures = 10, delegate = store), nightMode)
      .observeThemeMode().toList()

    assertEquals(emptyList<Int>(), nightMode.writes)
  }

  @Test
  fun `un fallo al escribir devuelve Left con CouldNotSave`() = runTest {
    val repository = repositoryOver(FailingWriteDataStore())

    val result = repository.setThemeMode(ThemeMode.Dark)

    assertEquals(SettingsError.CouldNotSave.left(), result)
  }
}
