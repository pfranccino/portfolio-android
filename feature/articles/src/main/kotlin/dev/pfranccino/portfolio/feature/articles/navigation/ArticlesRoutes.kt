package dev.pfranccino.portfolio.feature.articles.navigation

import kotlinx.serialization.Serializable

/** Ruta del grafo de articles. Es lo único público: sirve para entrar y para popUpTo. */
@Serializable
data object ArticlesGraph

internal sealed class ArticlesRoutes {
  @Serializable
  data object Start : ArticlesRoutes()
}
