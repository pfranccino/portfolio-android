package dev.pfranccino.portfolio.feature.series.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.pfranccino.portfolio.feature.series.ui.SeriesScreen

/** Grafo de series. En esta sección no tiene salidas. */
fun NavGraphBuilder.seriesGraph() {
  navigation<SeriesGraph>(startDestination = SeriesRoutes.Start) {
    composable<SeriesRoutes.Start> {
      SeriesScreen()
    }
  }
}
