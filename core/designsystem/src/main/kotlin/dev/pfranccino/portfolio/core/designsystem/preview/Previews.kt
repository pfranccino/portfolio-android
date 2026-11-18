package dev.pfranccino.portfolio.core.designsystem.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pfranccino.portfolio.core.designsystem.component.BottomBarItem
import dev.pfranccino.portfolio.core.designsystem.component.PortfolioBottomBar
import dev.pfranccino.portfolio.core.designsystem.component.PortfolioTopAppBar
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme

@Preview(name = "Light", showBackground = true, widthDp = 360)
@Preview(
  name = "Dark",
  showBackground = true,
  widthDp = 360,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(name = "Font 2x", showBackground = true, widthDp = 360, fontScale = 2f)
annotation class ThemePreviews

private val SampleItems = listOf(
  BottomBarItem("Inicio", PortfolioIcons.Home),
  BottomBarItem("Proyectos", PortfolioIcons.Terminal),
  BottomBarItem("Artículos", PortfolioIcons.Article),
  BottomBarItem("Serie", PortfolioIcons.SmartDisplay),
)

@Composable
private fun PreviewHost(content: @Composable () -> Unit) {
  PortfolioTheme {
    Surface(color = MaterialTheme.colorScheme.background, content = content)
  }
}

@ThemePreviews
@Composable
private fun PortfolioBottomBarPreview() {
  PreviewHost {
    var selected by remember { mutableIntStateOf(0) }
    PortfolioBottomBar(
      items = SampleItems,
      selectedIndex = selected,
      onItemClick = { selected = it },
    )
  }
}

@ThemePreviews
@Composable
private fun PortfolioTopAppBarPreview() {
  PreviewHost {
    Column {
      PortfolioTopAppBar(title = "Proyectos")
      PortfolioTopAppBar(
        title = "Ajustes",
        onBackClick = {},
        backContentDescription = "Volver",
      )
    }
  }
}

@ThemePreviews
@Composable
private fun PortfolioThemeSamplePreview() {
  PreviewHost {
    Column(modifier = Modifier.padding(20.dp)) {
      Text("Staff Android Engineer.", style = MaterialTheme.typography.displaySmall)
      Text("Un poco de mí.", style = MaterialTheme.typography.titleLarge)
      Text("Construyendo con Kotlin desde 2019.", style = PortfolioTheme.typography.lead)
      Text(
        "Hago apps Android desde 2019. Llegué a MACH en 2021.",
        style = MaterialTheme.typography.bodyLarge,
      )
      Text("+7", style = PortfolioTheme.typography.figure)
      Text("android-gradle-analyzer", style = PortfolioTheme.typography.repo)
      Text("2026-11-17 · Proyecto", style = MaterialTheme.typography.labelSmall)
      Text(
        "$ ./gradlew build",
        style = PortfolioTheme.typography.code,
        color = PortfolioTheme.colors.codeOk,
      )
      Text(
        "Disponible",
        style = MaterialTheme.typography.labelLarge,
        color = PortfolioTheme.colors.ok,
      )
    }
  }
}
