package dev.pfranccino.portfolio.feature.home.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.pfranccino.portfolio.core.designsystem.component.PortfolioIconButton
import dev.pfranccino.portfolio.core.designsystem.component.PortfolioTopAppBar
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme
import dev.pfranccino.portfolio.feature.home.R

/** Inicio. [onOpenSettings] se llama al tocar el ícono de Ajustes de la barra superior. */
@Composable
internal fun HomeScreen(
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
  ) {
    PortfolioTopAppBar(
      title = stringResource(R.string.feature_home_title),
      actions = {
        PortfolioIconButton(
          onClick = onOpenSettings,
          contentDescription = stringResource(R.string.feature_home_settings),
          icon = PortfolioIcons.Tune,
        )
      },
    )
  }
}

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(
  name = "Dark",
  showBackground = true,
  widthDp = 360,
  heightDp = 640,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun HomeScreenPreview() {
  PortfolioTheme {
    HomeScreen(onOpenSettings = {})
  }
}
