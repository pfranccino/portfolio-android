package dev.pfranccino.portfolio.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalPortfolioColors = staticCompositionLocalOf { LightPortfolioColors }
private val LocalPortfolioTypography = staticCompositionLocalOf { DefaultPortfolioTypography }

/** Acceso a los colores y estilos extendidos. Los de Material 3 siguen en `MaterialTheme`. */
object PortfolioTheme {
  val colors: PortfolioColors
    @Composable @ReadOnlyComposable get() = LocalPortfolioColors.current

  val typography: PortfolioTypography
    @Composable @ReadOnlyComposable get() = LocalPortfolioTypography.current
}

@Composable
fun PortfolioTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalPortfolioColors provides if (darkTheme) DarkPortfolioColors else LightPortfolioColors,
    LocalPortfolioTypography provides DefaultPortfolioTypography,
  ) {
    MaterialTheme(
      colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
      typography = PortfolioMaterialTypography,
      shapes = PortfolioShapes,
      content = content,
    )
  }
}
