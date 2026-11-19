package dev.pfranccino.portfolio.feature.settings.ui

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme
import dev.pfranccino.portfolio.feature.settings.R
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.LanguageUi
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiAction
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenContentTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val available = UiState(
    language = LanguageUi(
      selected = AppLanguage.System,
      systemDisplayLanguage = DisplayLanguage.Spanish,
      matchesPhone = true,
    ),
    themeMode = ThemeMode.Light,
    version = "1.0",
  )

  private fun string(id: Int): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

  private val radioButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)

  private fun show(state: UiState, onAction: (UiAction) -> Unit = {}) {
    composeRule.setContent {
      PortfolioTheme {
        SettingsScreenContent(state = state, onAction = onAction)
      }
    }
  }

  @Test
  fun optionRowsAreRadioButtonsWithOneSelectedPerGroup() {
    show(available)

    composeRule.onAllNodes(radioButton).assertCountEquals(6)
    composeRule.onAllNodes(radioButton and isSelected()).assertCountEquals(2)
    composeRule.onNodeWithText(string(R.string.feature_settings_option_light)).assertIsSelected()
  }

  @Test
  fun languageGroupIsHiddenWhenUnavailable() {
    show(available.copy(language = null))

    composeRule.onAllNodes(radioButton).assertCountEquals(3)
    composeRule.onNodeWithText(string(R.string.feature_settings_language_title)).assertDoesNotExist()
  }

  @Test
  fun tappingAnOptionReportsIt() {
    val actions = mutableListOf<UiAction>()
    show(available, onAction = { actions += it })

    composeRule.onNodeWithText(string(R.string.feature_settings_option_dark)).performClick()
    composeRule.onNodeWithText(string(R.string.feature_settings_language_name_english)).performClick()

    assertEquals(
      listOf(UiAction.OnThemeClick(ThemeMode.Dark), UiAction.OnLanguageClick(AppLanguage.English)),
      actions,
    )
  }

  @Test
  fun sourceCodeRowIsAnnouncedAndReportsTheTap() {
    val actions = mutableListOf<UiAction>()
    show(available, onAction = { actions += it })

    composeRule
      .onNodeWithContentDescription(string(R.string.feature_settings_source_code_description))
      .performClick()

    assertEquals(listOf<UiAction>(UiAction.OnSourceCodeClick), actions)
  }
}
