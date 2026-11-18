package dev.pfranccino.portfolio.feature.articles.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticlesGraphTest {
  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun startDestinationIsPlaceholder() {
    lateinit var navController: TestNavHostController
    composeRule.setContent {
      navController = TestNavHostController(LocalContext.current).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
      }
      NavHost(navController = navController, startDestination = ArticlesGraph) {
        articlesGraph()
      }
    }
    composeRule.waitForIdle()
    assertTrue(navController.currentDestination?.hasRoute<ArticlesRoutes.Start>() == true)
  }
}
