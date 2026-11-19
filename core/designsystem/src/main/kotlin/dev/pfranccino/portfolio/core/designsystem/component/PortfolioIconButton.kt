package dev.pfranccino.portfolio.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.preview.ThemePreviews
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme

/**
 * Botón de ícono con área táctil de 48 dp. La capa de estado (10 % al tocar o con foco, 8 % con
 * hover) tiene esquinas de 2 dp, y el foco de teclado dibuja un borde de 2 dp: `ink` en claro y
 * `accent` en oscuro, como la barra inferior.
 *
 * [contentDescription] es obligatoria: el ícono va solo.
 */
@Composable
fun PortfolioIconButton(
  onClick: () -> Unit,
  contentDescription: String,
  @DrawableRes icon: Int,
  modifier: Modifier = Modifier,
  tint: Color = MaterialTheme.colorScheme.onBackground,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val focused by interactionSource.collectIsFocusedAsState()
  val layerAlpha = rememberStateLayer(interactionSource)
  val layerColor = MaterialTheme.colorScheme.onSurface
  val shape = MaterialTheme.shapes.extraSmall
  Box(
    modifier = modifier
      .size(48.dp)
      .clip(shape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick,
      )
      .drawBehind { drawRect(layerColor.copy(alpha = layerAlpha)) }
      .then(if (focused) Modifier.border(2.dp, focusBorderColor(), shape) else Modifier),
    contentAlignment = Alignment.Center,
  ) {
    Icon(painter = painterResource(icon), contentDescription = contentDescription, tint = tint)
  }
}

@ThemePreviews
@Composable
private fun PortfolioIconButtonPreview() {
  PortfolioTheme {
    PortfolioIconButton(
      onClick = {},
      contentDescription = "Ajustes",
      icon = PortfolioIcons.Tune,
    )
  }
}
