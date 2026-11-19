package dev.pfranccino.portfolio.feature.home.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.pfranccino.portfolio.feature.home.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeGraphTest {
  @get:Rule
  val composeRule = createComposeRule()

  private fun show(onOpenSettings: () -> Unit): () -> TestNavHostController {
    lateinit var navController: TestNavHostController
    composeRule.setContent {
      navController = TestNavHostController(LocalContext.current).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
      }
      NavHost(navController = navController, startDestination = HomeGraph) {
        homeGraph(onOpenSettings = onOpenSettings)
      }
    }
    composeRule.waitForIdle()
    return { navController }
  }

  @Test
  fun startDestinationIsPlaceholder() {
    val navController = show(onOpenSettings = {})()
    assertTrue(navController.currentDestination?.hasRoute<HomeRoutes.Start>() == true)
  }

  @Test
  fun settingsIconReportsTheExit() {
    var opened = 0
    show(onOpenSettings = { opened++ })

    val label = InstrumentationRegistry.getInstrumentation().targetContext
      .getString(R.string.feature_home_settings)
    composeRule.onNodeWithContentDescription(label).performClick()

    assertEquals(1, opened)
  }
}
