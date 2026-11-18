package dev.pfranccino.portfolio.feature.home.navigation

import kotlinx.serialization.Serializable

/** Ruta del grafo de home. Es lo único público: sirve para entrar y para popUpTo. */
@Serializable
data object HomeGraph

internal sealed class HomeRoutes {
  @Serializable
  data object Start : HomeRoutes()
}
