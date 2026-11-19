package dev.pfranccino.portfolio.feature.settings.data.mapper

import android.app.UiModeManager
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode

/** Valor que se guarda en DataStore. Son textos fijos, no el orden del enum, para que un cambio de orden no altere lo guardado. */
internal fun ThemeMode.toStoredValue(): String = when (this) {
  ThemeMode.System -> "system"
  ThemeMode.Light -> "light"
  ThemeMode.Dark -> "dark"
}

/** Modo a partir del valor guardado. Sin valor o desconocido, [ThemeMode.System]. */
internal fun String?.toThemeMode(): ThemeMode = when (this) {
  "light" -> ThemeMode.Light
  "dark" -> ThemeMode.Dark
  else -> ThemeMode.System
}

/** Constante de `UiModeManager` que fija el modo de noche de la app. System sigue al teléfono. */
internal fun ThemeMode.toNightMode(): Int = when (this) {
  ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
  ThemeMode.Light -> UiModeManager.MODE_NIGHT_NO
  ThemeMode.Dark -> UiModeManager.MODE_NIGHT_YES
}
