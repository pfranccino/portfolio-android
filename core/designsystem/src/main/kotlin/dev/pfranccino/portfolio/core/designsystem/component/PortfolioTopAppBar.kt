package dev.pfranccino.portfolio.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioSpacing

/**
 * Barra superior sin estado. Mide 64 dp como mínimo y crece con el texto. Su color es `background`
 * siempre, también al hacer scroll, y no tiene elevación.
 *
 * Con [onBackClick] muestra la flecha para volver; entonces [backContentDescription] es obligatoria
 * en la práctica, porque la flecha va sola.
 */
@Composable
fun PortfolioTopAppBar(
  title: String,
  modifier: Modifier = Modifier,
  onBackClick: (() -> Unit)? = null,
  backContentDescription: String? = null,
) {
  Row(
    modifier = modifier
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
      .heightIn(min = 64.dp)
      .padding(
        start = if (onBackClick != null) 4.dp else PortfolioSpacing.ScreenMargin,
        end = PortfolioSpacing.ScreenMargin,
        top = PortfolioSpacing.Space8,
        bottom = PortfolioSpacing.Space8,
      ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    if (onBackClick != null) {
      IconButton(onClick = onBackClick) {
        Icon(
          painter = painterResource(PortfolioIcons.ArrowBack),
          contentDescription = backContentDescription,
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }
    }
    Text(
      text = title,
      style = MaterialTheme.typography.headlineMedium,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.semantics { heading() },
    )
  }
}
