package dev.pfranccino.portfolio.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pfranccino.portfolio.core.common.fold
import dev.pfranccino.portfolio.core.ui.mvi.MVI
import dev.pfranccino.portfolio.core.ui.mvi.mvi
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.GetAppVersionUseCase
import dev.pfranccino.portfolio.feature.settings.domain.GetLanguageSettingsUseCase
import dev.pfranccino.portfolio.feature.settings.domain.LanguageSettings
import dev.pfranccino.portfolio.feature.settings.domain.ObserveThemeModeUseCase
import dev.pfranccino.portfolio.feature.settings.domain.SetLanguageUseCase
import dev.pfranccino.portfolio.feature.settings.domain.SetThemeModeUseCase
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.SideEffect
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiAction
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
  private val observeThemeMode: ObserveThemeModeUseCase,
  private val setThemeMode: SetThemeModeUseCase,
  private val getLanguageSettings: GetLanguageSettingsUseCase,
  private val setLanguage: SetLanguageUseCase,
  private val getAppVersion: GetAppVersionUseCase,
) : ViewModel(), MVI<UiState, UiAction, SideEffect> by mvi(UiState()) {

  private var started = false

  override fun onAction(action: UiAction) {
    when (action) {
      UiAction.OnStart -> start()
      is UiAction.OnLanguageClick -> selectLanguage(action.language)
      is UiAction.OnThemeClick -> selectTheme(action.mode)
      UiAction.OnSourceCodeClick ->
        viewModelScope.emitSideEffect(SideEffect.OpenLink(SettingsContract.SOURCE_CODE_URL))
    }
  }

  private fun start() {
    // El idioma se lee en cada arranque: un cambio desde los ajustes del sistema recrea la
    // actividad, pero este ViewModel sobrevive.
    readLanguage()
    if (started) return
    started = true
    updateUiState { copy(version = getAppVersion()) }
    observeThemeMode()
      .onEach { mode -> updateUiState { copy(themeMode = mode) } }
      .launchIn(viewModelScope)
  }

  private fun readLanguage() {
    val language = when (val settings = getLanguageSettings()) {
      LanguageSettings.Unavailable -> null
      is LanguageSettings.Available -> SettingsContract.LanguageUi(
        selected = settings.selected,
        systemDisplayLanguage = settings.systemDisplayLanguage,
        matchesPhone = settings.matchesPhone,
      )
    }
    updateUiState { copy(language = language) }
  }

  private fun selectLanguage(language: AppLanguage) {
    setLanguage(language)
    readLanguage()
  }

  /**
   * La selección sale solo del tema observado: si guardar falla, el tema observado no cambia y la
   * pantalla sigue mostrando la opción anterior.
   */
  private fun selectTheme(mode: ThemeMode) {
    viewModelScope.launch {
      setThemeMode(mode).fold(
        onLeft = { emitSideEffect(SideEffect.ThemeSaveFailed) },
        onRight = {},
      )
    }
  }
}
