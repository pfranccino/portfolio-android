package dev.pfranccino.portfolio.feature.settings.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.LanguageUi
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiState

private val SampleState = UiState(
  language = LanguageUi(
    selected = AppLanguage.System,
    systemDisplayLanguage = DisplayLanguage.Spanish,
    matchesPhone = true,
  ),
  themeMode = ThemeMode.System,
  version = "1.0",
)

@Composable
private fun PreviewHost(state: UiState) {
  PortfolioTheme {
    SettingsScreenContent(state = state)
  }
}

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun SettingsLightPreview() {
  PreviewHost(SampleState)
}

@Preview(
  name = "Dark",
  showBackground = true,
  widthDp = 360,
  heightDp = 720,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SettingsDarkPreview() {
  PreviewHost(SampleState.copy(themeMode = ThemeMode.Dark))
}

@Preview(name = "English", showBackground = true, widthDp = 360, heightDp = 720, locale = "en")
@Composable
private fun SettingsEnglishPreview() {
  PreviewHost(
    SampleState.copy(
      language = LanguageUi(
        selected = AppLanguage.System,
        systemDisplayLanguage = DisplayLanguage.English,
        matchesPhone = true,
      ),
    ),
  )
}

@Preview(name = "Font 2x", showBackground = true, widthDp = 360, heightDp = 720, fontScale = 2f)
@Composable
private fun SettingsFontScalePreview() {
  PreviewHost(SampleState)
}

@Preview(name = "Language unavailable", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun SettingsLanguageUnavailablePreview() {
  PreviewHost(SampleState.copy(language = null, themeMode = ThemeMode.Light))
}
