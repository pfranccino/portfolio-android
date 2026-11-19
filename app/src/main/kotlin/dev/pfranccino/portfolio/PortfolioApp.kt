package dev.pfranccino.portfolio

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.pfranccino.portfolio.core.designsystem.component.BottomBarItem
import dev.pfranccino.portfolio.core.designsystem.component.PortfolioBottomBar
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioMotion
import dev.pfranccino.portfolio.core.designsystem.theme.rememberPortfolioDetailTransitions
import dev.pfranccino.portfolio.feature.articles.navigation.ArticlesGraph
import dev.pfranccino.portfolio.feature.articles.navigation.articlesGraph
import dev.pfranccino.portfolio.feature.home.navigation.HomeGraph
import dev.pfranccino.portfolio.feature.home.navigation.homeGraph
import dev.pfranccino.portfolio.feature.projects.navigation.ProjectsGraph
import dev.pfranccino.portfolio.feature.projects.navigation.projectsGraph
import dev.pfranccino.portfolio.feature.series.navigation.SeriesGraph
import dev.pfranccino.portfolio.feature.settings.navigation.SettingsGraph
import dev.pfranccino.portfolio.feature.settings.navigation.navigateToSettings
import dev.pfranccino.portfolio.feature.settings.navigation.settingsGraph
import dev.pfranccino.portfolio.feature.series.navigation.seriesGraph
import kotlin.reflect.KClass

private data class TopLevelDestination(
  @param:StringRes val label: Int,
  @param:DrawableRes val icon: Int,
  val graph: Any,
  val graphClass: KClass<*>,
)

private val TopLevelDestinations = listOf(
  TopLevelDestination(R.string.tab_home, PortfolioIcons.Home, HomeGraph, HomeGraph::class),
  TopLevelDestination(R.string.tab_projects, PortfolioIcons.Terminal, ProjectsGraph, ProjectsGraph::class),
  TopLevelDestination(R.string.tab_articles, PortfolioIcons.Article, ArticlesGraph, ArticlesGraph::class),
  TopLevelDestination(R.string.tab_series, PortfolioIcons.SmartDisplay, SeriesGraph, SeriesGraph::class),
)

private val TabExitTransition: ExitTransition = fadeOut(
  animationSpec = tween(
    durationMillis = PortfolioMotion.TabExitMillis,
    easing = PortfolioMotion.EmphasizedAccelerate,
  ),
)

private val TabEnterTransition: EnterTransition = fadeIn(
  animationSpec = tween(
    durationMillis = PortfolioMotion.TabEnterMillis,
    delayMillis = PortfolioMotion.TabExitMillis,
    easing = PortfolioMotion.EmphasizedDecelerate,
  ),
)

@Composable
fun PortfolioApp(navController: NavHostController = rememberNavController()) {
  val backStackEntry by navController.currentBackStackEntryAsState()
  val destination = backStackEntry?.destination

  val selectedIndex = TopLevelDestinations.indexOfFirst { top ->
    destination?.hierarchy?.any { it.hasRoute(top.graphClass) } == true
  }.coerceAtLeast(0)
  val showBottomBar = destination.isTopLevelRoot()

  val items = TopLevelDestinations.map { BottomBarItem(stringResource(it.label), it.icon) }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0),
    bottomBar = {
      if (showBottomBar) {
        PortfolioBottomBar(
          items = items,
          selectedIndex = selectedIndex,
          onItemClick = { index ->
            navController.navigateToTab(TopLevelDestinations[index], reselected = index == selectedIndex)
          },
        )
      }
    },
  ) { innerPadding ->
    val detail = rememberPortfolioDetailTransitions()
    NavHost(
      navController = navController,
      startDestination = HomeGraph,
      modifier = Modifier.padding(innerPadding),
      enterTransition = {
        if (targetState.destination.isFullScreenGraph()) detail.enter else TabEnterTransition
      },
      exitTransition = {
        if (targetState.destination.isFullScreenGraph()) detail.exit else TabExitTransition
      },
      popEnterTransition = {
        if (initialState.destination.isFullScreenGraph()) detail.popEnter else TabEnterTransition
      },
      popExitTransition = {
        if (initialState.destination.isFullScreenGraph()) detail.popExit else TabExitTransition
      },
    ) {
      homeGraph(onOpenSettings = { navController.navigateToSettings() })
      projectsGraph()
      articlesGraph()
      seriesGraph()
      settingsGraph(onBack = { navController.popBackStack() })
    }
  }
}

/** Grafos que se abren a pantalla completa, fuera de las pestañas, y usan las transiciones de detalle. */
private val FullScreenGraphs: List<KClass<*>> = listOf(SettingsGraph::class)

private fun NavDestination.isFullScreenGraph(): Boolean =
  hierarchy.any { node -> FullScreenGraphs.any { node.hasRoute(it) } }

/** La barra solo se muestra en la pantalla inicial de alguno de los cuatro grafos. */
private fun NavDestination?.isTopLevelRoot(): Boolean {
  if (this == null) return false
  return TopLevelDestinations.any { top ->
    val graph = hierarchy.firstOrNull { it.hasRoute(top.graphClass) } as? androidx.navigation.NavGraph
    graph != null && graph.findStartDestination().id == id
  }
}

private fun NavHostController.navigateToTab(top: TopLevelDestination, reselected: Boolean) {
  if (reselected) {
    // Vuelve a la raíz de la pestaña sin salir de ella.
    val root = graph.findNode(top.graph)?.let { (it as androidx.navigation.NavGraph).findStartDestination().id }
    if (root != null) popBackStack(root, inclusive = false)
  } else {
    navigate(top.graph) {
      popUpTo(graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }
}
