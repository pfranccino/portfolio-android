package dev.pfranccino.portfolio.feature.settings.navigation

import kotlinx.serialization.Serializable

/** Ruta del grafo de ajustes. Es lo único público: sirve para entrar y para popUpTo. */
@Serializable
data object SettingsGraph

internal sealed class SettingsRoutes {
  @Serializable
  data object Start : SettingsRoutes()
}
