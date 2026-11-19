package dev.pfranccino.portfolio.feature.settings.presentation

import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode

internal object SettingsContract {
  /** Enlace del código fuente que abre la fila "Código fuente". */
  const val SOURCE_CODE_URL = "https://github.com/pfranccino/portfolio-android"

  /**
   * Estado del grupo Idioma.
   *
   * @property selected la opción que tiene la app ahora.
   * @property systemDisplayLanguage el idioma en que se ve la app cuando sigue al teléfono.
   * @property matchesPhone falso si el teléfono está en un idioma que la app no tiene.
   */
  data class LanguageUi(
    val selected: AppLanguage,
    val systemDisplayLanguage: DisplayLanguage,
    val matchesPhone: Boolean,
  )

  /**
   * @property language `null` cuando el sistema no ofrece idioma por app: el grupo no se muestra.
   * @property themeMode el modo guardado. Solo cambia cuando el almacenamiento lo emite.
   * @property version el nombre de versión de la app; vacío hasta que arranca la pantalla.
   */
  data class UiState(
    val language: LanguageUi? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val version: String = "",
  )

  sealed interface UiAction {
    data object OnStart : UiAction
    data class OnLanguageClick(val language: AppLanguage) : UiAction
    data class OnThemeClick(val mode: ThemeMode) : UiAction
    data object OnSourceCodeClick : UiAction
  }

  sealed interface SideEffect {
    /** No se pudo guardar el tema: la pantalla muestra un aviso breve. */
    data object ThemeSaveFailed : SideEffect

    /** La pantalla abre [url] con el abridor de enlaces de `:core:ui`. */
    data class OpenLink(val url: String) : SideEffect
  }
}
