package dev.pfranccino.portfolio.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.pfranccino.portfolio.feature.home.ui.HomeScreen

/**
 * Grafo de home. [onOpenSettings] avisa que la persona pidió abrir Ajustes; la app decide adónde
 * va.
 */
fun NavGraphBuilder.homeGraph(onOpenSettings: () -> Unit) {
  navigation<HomeGraph>(startDestination = HomeRoutes.Start) {
    composable<HomeRoutes.Start> {
      HomeScreen(onOpenSettings = onOpenSettings)
    }
  }
}
