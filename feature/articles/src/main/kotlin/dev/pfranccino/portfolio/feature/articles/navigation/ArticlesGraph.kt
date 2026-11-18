package dev.pfranccino.portfolio.feature.articles.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import dev.pfranccino.portfolio.feature.articles.ui.ArticlesScreen

/** Grafo de articles. En esta sección no tiene salidas. */
fun NavGraphBuilder.articlesGraph() {
  navigation<ArticlesGraph>(startDestination = ArticlesRoutes.Start) {
    composable<ArticlesRoutes.Start> {
      ArticlesScreen()
    }
  }
}
