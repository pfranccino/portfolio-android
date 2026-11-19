package dev.pfranccino.portfolio.feature.settings.data

import dev.pfranccino.portfolio.core.common.Either
import dev.pfranccino.portfolio.core.common.left
import dev.pfranccino.portfolio.core.common.right
import dev.pfranccino.portfolio.feature.settings.data.local.StoredTheme
import dev.pfranccino.portfolio.feature.settings.data.local.ThemePreferencesDataSource
import dev.pfranccino.portfolio.feature.settings.data.mapper.toNightMode
import dev.pfranccino.portfolio.feature.settings.data.mapper.toStoredValue
import dev.pfranccino.portfolio.feature.settings.data.mapper.toThemeMode
import dev.pfranccino.portfolio.feature.settings.domain.SettingsError
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import dev.pfranccino.portfolio.feature.settings.domain.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore es la fuente de verdad del tema. Además, el modo de noche de la app en el sistema
 * se fija igual, para que el splash del arranque en frío coincida con el tema.
 */
@Singleton
internal class ThemeRepositoryImpl @Inject constructor(
  private val source: ThemePreferencesDataSource,
  private val appNightMode: AppNightModeSystem,
) : ThemeRepository {

  // Estado de proceso: el alineado con el sistema se hace una sola vez por arranque.
  private val alignedThisProcess = AtomicBoolean(false)

  override fun observeThemeMode(): Flow<ThemeMode> =
    source.storedTheme
      // Solo un valor realmente guardado alinea el sistema; un fallo de lectura no lo cambia.
      .onEach { stored -> stored.savedMode()?.let { mode -> alignSystemOnce(mode) } }
      .map { stored -> stored.savedMode() ?: ThemeMode.System }

  override suspend fun setThemeMode(mode: ThemeMode): Either<SettingsError, Unit> =
    try {
      source.saveThemeValue(mode.toStoredValue())
      // Solo después de guardar: si DataStore falla, el sistema no cambia.
      appNightMode.write(mode.toNightMode())
      Unit.right()
    } catch (e: IOException) {
      SettingsError.CouldNotSave.left()
    }

  /**
   * Fija el modo de noche del sistema con el tema guardado, una vez por proceso. Cubre una
   * restauración o una reinstalación, donde el sistema no conserva el modo que tenía DataStore.
   * Escribir el mismo valor no cambia nada.
   */
  private fun alignSystemOnce(mode: ThemeMode) {
    if (alignedThisProcess.compareAndSet(false, true)) appNightMode.write(mode.toNightMode())
  }

  private fun StoredTheme.savedMode(): ThemeMode? =
    (this as? StoredTheme.Saved)?.value?.toThemeMode()
}
