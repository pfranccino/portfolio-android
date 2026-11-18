package dev.pfranccino.portfolio.feature.projects.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.pfranccino.portfolio.feature.projects.ui.ProjectsScreen

/** Grafo de projects. En esta sección no tiene salidas. */
fun NavGraphBuilder.projectsGraph() {
  navigation<ProjectsGraph>(startDestination = ProjectsRoutes.Start) {
    composable<ProjectsRoutes.Start> {
      ProjectsScreen()
    }
  }
}
