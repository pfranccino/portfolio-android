package dev.pfranccino.portfolio.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.pfranccino.portfolio.feature.home.ui.HomeScreen

/** Grafo de home. En esta sección no tiene salidas. */
fun NavGraphBuilder.homeGraph() {
  navigation<HomeGraph>(startDestination = HomeRoutes.Start) {
    composable<HomeRoutes.Start> {
      HomeScreen()
    }
  }
}
