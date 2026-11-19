package dev.pfranccino.portfolio.feature.settings.domain

import dev.pfranccino.portfolio.core.common.Either
import dev.pfranccino.portfolio.core.common.left
import dev.pfranccino.portfolio.core.common.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class FakeThemeRepository(var failOnSave: Boolean = false) : ThemeRepository {
  val mode = MutableStateFlow(ThemeMode.System)

  override fun observeThemeMode(): Flow<ThemeMode> = mode

  override suspend fun setThemeMode(mode: ThemeMode): Either<SettingsError, Unit> {
    if (failOnSave) return SettingsError.CouldNotSave.left()
    this.mode.value = mode
    return Unit.right()
  }
}

private class FakeLanguageRepository(
  var settings: LanguageSettings = LanguageSettings.Unavailable,
) : LanguageRepository {
  var applied: AppLanguage? = null

  override fun getLanguageSettings(): LanguageSettings = settings

  override fun setLanguage(language: AppLanguage) {
    applied = language
  }
}

private class FakeAppInfoRepository(val version: String) : AppInfoRepository {
  override fun getVersionName(): String = version
}

class SettingsUseCasesTest {
  @Test
  fun `observar el tema emite el modo del repositorio`() = runTest {
    val repository = FakeThemeRepository()
    val observe = ObserveThemeModeUseCase(repository)

    assertEquals(ThemeMode.System, observe().first())
    repository.mode.value = ThemeMode.Dark
    assertEquals(ThemeMode.Dark, observe().first())
  }

  @Test
  fun `guardar el tema devuelve Right y lo aplica`() = runTest {
    val repository = FakeThemeRepository()

    val result = SetThemeModeUseCase(repository)(ThemeMode.Light)

    assertEquals(Unit.right(), result)
    assertEquals(ThemeMode.Light, repository.mode.value)
  }

  @Test
  fun `guardar el tema devuelve Left si falla`() = runTest {
    val repository = FakeThemeRepository(failOnSave = true)

    val result = SetThemeModeUseCase(repository)(ThemeMode.Dark)

    assertEquals(SettingsError.CouldNotSave.left(), result)
    assertEquals(ThemeMode.System, repository.mode.value)
  }

  @Test
  fun `el estado del idioma viene del repositorio`() {
    val available = LanguageSettings.Available(
      selected = AppLanguage.System,
      systemDisplayLanguage = DisplayLanguage.Spanish,
      matchesPhone = false,
    )
    val repository = FakeLanguageRepository(available)
    val get = GetLanguageSettingsUseCase(repository)

    assertEquals(available, get())
    repository.settings = LanguageSettings.Unavailable
    assertEquals(LanguageSettings.Unavailable, get())
  }

  @Test
  fun `elegir idioma lo pasa al repositorio`() {
    val repository = FakeLanguageRepository()

    SetLanguageUseCase(repository)(AppLanguage.English)

    assertEquals(AppLanguage.English, repository.applied)
  }

  @Test
  fun `la versión viene del repositorio`() {
    assertEquals("1.0", GetAppVersionUseCase(FakeAppInfoRepository("1.0"))())
  }
}
