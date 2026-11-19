package dev.pfranccino.portfolio.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Fila tocable de ancho completo, con los estados del diseño: capa de 10 % al tocar o con foco y de
 * 8 % con hover, esquinas de 2 dp y, con foco de teclado, borde de 2 dp (`ink` en claro, `accent`
 * en oscuro). Va antes del `padding` de la fila, para que la capa cubra toda su área.
 */
@Composable
fun Modifier.portfolioClickableRow(
  onClick: () -> Unit,
  role: Role = Role.Button,
): Modifier {
  val interactionSource = remember { MutableInteractionSource() }
  return this
    .clip(MaterialTheme.shapes.extraSmall)
    .clickable(
      interactionSource = interactionSource,
      indication = null,
      role = role,
      onClick = onClick,
    )
    .rowStateLayer(interactionSource)
}

/**
 * Como [portfolioClickableRow], pero para una opción excluyente: `selectable` con [role] de
 * `RadioButton`. El grupo sigue necesitando `selectableGroup()` en su contenedor.
 */
@Composable
fun Modifier.portfolioSelectableRow(
  selected: Boolean,
  onClick: () -> Unit,
  role: Role = Role.RadioButton,
): Modifier {
  val interactionSource = remember { MutableInteractionSource() }
  return this
    .clip(MaterialTheme.shapes.extraSmall)
    .selectable(
      selected = selected,
      interactionSource = interactionSource,
      indication = null,
      role = role,
      onClick = onClick,
    )
    .rowStateLayer(interactionSource)
}

/** Capa de estado y borde de foco, dentro del recorte de 2 dp. */
@Composable
private fun Modifier.rowStateLayer(interactionSource: MutableInteractionSource): Modifier {
  val focused by interactionSource.collectIsFocusedAsState()
  val layerAlpha = rememberStateLayer(interactionSource)
  val layerColor = MaterialTheme.colorScheme.onSurface
  val borderColor = focusBorderColor()
  val shape = MaterialTheme.shapes.extraSmall
  return this
    .drawBehind { drawRect(layerColor.copy(alpha = layerAlpha)) }
    .then(if (focused) Modifier.border(2.dp, borderColor, shape) else Modifier)
}
