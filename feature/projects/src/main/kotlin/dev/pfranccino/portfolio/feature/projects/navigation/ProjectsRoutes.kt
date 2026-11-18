package dev.pfranccino.portfolio.feature.projects.navigation

import kotlinx.serialization.Serializable

/** Ruta del grafo de projects. Es lo único público: sirve para entrar y para popUpTo. */
@Serializable
data object ProjectsGraph

internal sealed class ProjectsRoutes {
  @Serializable
  data object Start : ProjectsRoutes()
}
