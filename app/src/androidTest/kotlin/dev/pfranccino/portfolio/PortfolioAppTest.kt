package dev.pfranccino.portfolio

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PortfolioAppTest {
  @get:Rule
  val composeRule = createAndroidComposeRule<ComponentActivity>()

  private fun label(id: Int) = composeRule.activity.getString(id)

  private fun tab(id: Int) = hasText(label(id)) and hasClickAction()

  private fun title(id: Int) = hasText(label(id)) and androidx.compose.ui.test.hasClickAction().not()

  @Test
  fun startsOnHomeWithTitle() {
    composeRule.setContent { PortfolioTheme { PortfolioApp() } }
    composeRule.onNode(tab(R.string.tab_home)).assertIsSelected()
    composeRule.onNode(title(R.string.tab_home)).assertExists()
  }

  @Test
  fun tappingProjectsShowsProjects() {
    composeRule.setContent { PortfolioTheme { PortfolioApp() } }
    composeRule.onNode(tab(R.string.tab_projects)).performClick()
    composeRule.waitForIdle()
    composeRule.onNode(tab(R.string.tab_projects)).assertIsSelected()
    composeRule.onNode(title(R.string.tab_projects)).assertExists()
  }

  @Test
  fun backReturnsToHome() {
    composeRule.setContent { PortfolioTheme { PortfolioApp() } }
    composeRule.onNode(tab(R.string.tab_projects)).performClick()
    composeRule.waitForIdle()
    composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
    composeRule.waitForIdle()
    composeRule.onNode(tab(R.string.tab_home)).assertIsSelected()
    composeRule.onNode(title(R.string.tab_home)).assertExists()
  }
}
