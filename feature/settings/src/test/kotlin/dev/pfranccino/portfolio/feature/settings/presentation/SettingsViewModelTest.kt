package dev.pfranccino.portfolio.feature.settings.presentation

import dev.pfranccino.portfolio.core.common.Either
import dev.pfranccino.portfolio.core.common.left
import dev.pfranccino.portfolio.core.common.right
import dev.pfranccino.portfolio.feature.settings.domain.AppInfoRepository
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.GetAppVersionUseCase
import dev.pfranccino.portfolio.feature.settings.domain.GetLanguageSettingsUseCase
import dev.pfranccino.portfolio.feature.settings.domain.LanguageRepository
import dev.pfranccino.portfolio.feature.settings.domain.LanguageSettings
import dev.pfranccino.portfolio.feature.settings.domain.ObserveThemeModeUseCase
import dev.pfranccino.portfolio.feature.settings.domain.SetLanguageUseCase
import dev.pfranccino.portfolio.feature.settings.domain.SetThemeModeUseCase
import dev.pfranccino.portfolio.feature.settings.domain.SettingsError
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import dev.pfranccino.portfolio.feature.settings.domain.ThemeRepository
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.SideEffect
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private class FakeThemeRepository(var failOnSave: Boolean = false) : ThemeRepository {
  val mode = MutableStateFlow(ThemeMode.System)
  var observeCalls = 0

  override fun observeThemeMode(): Flow<ThemeMode> {
    observeCalls++
    return mode
  }

  override suspend fun setThemeMode(mode: ThemeMode): Either<SettingsError, Unit> {
    if (failOnSave) return SettingsError.CouldNotSave.left()
    this.mode.value = mode
    return Unit.right()
  }
}

private class FakeLanguageRepository(
  var settings: LanguageSettings = LanguageSettings.Unavailable,
) : LanguageRepository {
  var reads = 0
  var applied: AppLanguage? = null

  override fun getLanguageSettings(): LanguageSettings {
    reads++
    return settings
  }

  override fun setLanguage(language: AppLanguage) {
    applied = language
    val current = settings
    if (current is LanguageSettings.Available) settings = current.copy(selected = language)
  }
}

private class FakeAppInfoRepository : AppInfoRepository {
  var reads = 0

  override fun getVersionName(): String {
    reads++
    return "1.0"
  }
}

private val SpanishSystem = LanguageSettings.Available(
  selected = AppLanguage.System,
  systemDisplayLanguage = DisplayLanguage.Spanish,
  matchesPhone = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
  private val theme = FakeThemeRepository()
  private val language = FakeLanguageRepository(SpanishSystem)
  private val appInfo = FakeAppInfoRepository()

  @BeforeEach
  fun setUp() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
  }

  private fun viewModel() = SettingsViewModel(
    observeThemeMode = ObserveThemeModeUseCase(theme),
    setThemeMode = SetThemeModeUseCase(theme),
    getLanguageSettings = GetLanguageSettingsUseCase(language),
    setLanguage = SetLanguageUseCase(language),
    getAppVersion = GetAppVersionUseCase(appInfo),
  )

  @Test
  fun `crear el ViewModel no llama a ningún caso de uso`() {
    viewModel()

    assertEquals(0, theme.observeCalls)
    assertEquals(0, language.reads)
    assertEquals(0, appInfo.reads)
  }

  @Test
  fun `OnStart carga versión, tema e idioma`() {
    theme.mode.value = ThemeMode.Dark
    val viewModel = viewModel()

    viewModel.onAction(UiAction.OnStart)

    val state = viewModel.uiState.value
    assertEquals("1.0", state.version)
    assertEquals(ThemeMode.Dark, state.themeMode)
    assertEquals(AppLanguage.System, state.language?.selected)
  }

  @Test
  fun `OnStart enviado dos veces observa el tema una vez pero relee el idioma`() {
    val viewModel = viewModel()

    viewModel.onAction(UiAction.OnStart)
    language.settings = SpanishSystem.copy(selected = AppLanguage.English)
    viewModel.onAction(UiAction.OnStart)

    assertEquals(1, theme.observeCalls)
    assertEquals(1, appInfo.reads)
    assertEquals(2, language.reads)
    assertEquals(AppLanguage.English, viewModel.uiState.value.language?.selected)
  }

  @Test
  fun `sin idioma por app el grupo no tiene estado`() {
    language.settings = LanguageSettings.Unavailable
    val viewModel = viewModel()

    viewModel.onAction(UiAction.OnStart)

    assertNull(viewModel.uiState.value.language)
  }

  @Test
  fun `elegir un idioma lo aplica y lo muestra marcado`() {
    val viewModel = viewModel()
    viewModel.onAction(UiAction.OnStart)

    viewModel.onAction(UiAction.OnLanguageClick(AppLanguage.English))

    assertEquals(AppLanguage.English, language.applied)
    assertEquals(AppLanguage.English, viewModel.uiState.value.language?.selected)
  }

  @Test
  fun `elegir un tema lo guarda y la selección lo muestra`() {
    val viewModel = viewModel()
    viewModel.onAction(UiAction.OnStart)

    viewModel.onAction(UiAction.OnThemeClick(ThemeMode.Light))

    assertEquals(ThemeMode.Light, viewModel.uiState.value.themeMode)
  }

  @Test
  fun `si guardar el tema falla la selección sigue en la anterior y se avisa`() = runTest {
    theme.failOnSave = true
    val viewModel = viewModel()
    viewModel.onAction(UiAction.OnStart)

    viewModel.onAction(UiAction.OnThemeClick(ThemeMode.Dark))

    assertEquals(ThemeMode.System, viewModel.uiState.value.themeMode)
    assertEquals(SideEffect.ThemeSaveFailed, viewModel.sideEffect.first())
  }

  @Test
  fun `tocar el código fuente emite el enlace una vez`() = runTest {
    val viewModel = viewModel()

    viewModel.onAction(UiAction.OnSourceCodeClick)

    assertEquals(
      SideEffect.OpenLink("https://github.com/pfranccino/portfolio-android"),
      viewModel.sideEffect.first(),
    )
  }
}
