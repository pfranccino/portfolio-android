package dev.pfranccino.portfolio.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import dev.pfranccino.portfolio.feature.settings.ui.SettingsScreen

/**
 * Grafo de ajustes. Su única salida es [onBack]: la llama la flecha de la barra. El atrás del
 * sistema lo maneja el `NavHost` de la app, que saca este destino de la pila. No declara
 * transiciones: las aplica el `NavHost` por destino.
 */
fun NavGraphBuilder.settingsGraph(onBack: () -> Unit) {
  navigation<SettingsGraph>(startDestination = SettingsRoutes.Start) {
    composable<SettingsRoutes.Start> {
      SettingsScreen(onBack = onBack)
    }
  }
}

/** Abre Ajustes. Por defecto evita duplicarlo con un doble toque. */
fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
  navigate(SettingsGraph, navOptions ?: navOptions { launchSingleTop = true })
}
