package dev.pfranccino.portfolio.feature.settings.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.pfranccino.portfolio.core.testing.HiltComponentActivity
import dev.pfranccino.portfolio.feature.settings.R
import kotlinx.serialization.Serializable
import dev.pfranccino.portfolio.core.common.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/** Destino de prueba que hace de pantalla anterior a Ajustes. */
@Serializable
private data object DummyStart

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsGraphTest {
  private val hiltRule = HiltAndroidRule(this)
  private val composeRule = createAndroidComposeRule<HiltComponentActivity>()

  @get:Rule
  val rules: RuleChain = RuleChain.outerRule(hiltRule).around(composeRule)

  /** El ámbito de aplicación de esta prueba: al cancelarlo, el DataStore suelta su archivo. */
  @Inject
  @ApplicationScope
  lateinit var appScope: CoroutineScope

  private lateinit var navController: TestNavHostController
  private var onBackCalls = 0

  @After
  fun cancelAppScope() {
    runBlocking { appScope.coroutineContext[Job]?.cancelAndJoin() }
  }

  @Before
  fun setUp() {
    hiltRule.inject()
    onBackCalls = 0
    composeRule.setContent {
      navController = TestNavHostController(LocalContext.current).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
      }
      NavHost(navController = navController, startDestination = DummyStart) {
        composable<DummyStart> {}
        settingsGraph(
          onBack = {
            onBackCalls++
            navController.popBackStack()
          },
        )
      }
    }
    composeRule.waitForIdle()
  }

  private fun openSettings() {
    composeRule.runOnUiThread { navController.navigateToSettings() }
    composeRule.waitForIdle()
  }

  @Test
  fun navigatingToSettingsLandsOnItsStartDestination() {
    openSettings()

    assertTrue(navController.currentDestination?.hasRoute<SettingsRoutes.Start>() == true)
  }

  @Test
  fun backArrowCallsOnBackOnce() {
    openSettings()
    val back = InstrumentationRegistry.getInstrumentation().targetContext
      .getString(R.string.feature_settings_back)

    composeRule.onNodeWithContentDescription(back).performClick()
    composeRule.waitForIdle()

    assertEquals(1, onBackCalls)
    assertTrue(navController.currentDestination?.hasRoute<DummyStart>() == true)
  }

  @Test
  fun systemBackReturnsThroughTheNavHostWithoutCallingOnBack() {
    openSettings()

    composeRule.runOnUiThread {
      composeRule.activity.onBackPressedDispatcher.onBackPressed()
    }
    composeRule.waitForIdle()

    assertEquals(0, onBackCalls)
    assertTrue(navController.currentDestination?.hasRoute<DummyStart>() == true)
  }
}
