package dev.pfranccino.portfolio.feature.series.navigation

import kotlinx.serialization.Serializable

/** Ruta del grafo de series. Es lo único público: sirve para entrar y para popUpTo. */
@Serializable
data object SeriesGraph

internal sealed class SeriesRoutes {
  @Serializable
  data object Start : SeriesRoutes()
}
