package dev.pfranccino.portfolio.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.border
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme

@Immutable
data class BottomBarItem(
  val label: String,
  @param:DrawableRes val icon: Int,
)

private val ColumnLabelPadding = 4.dp
private val GridMinCellHeight = 56.dp
private val GridPadding = 4.dp
private const val NoFocus = -1

/** Foco de teclado: borde en `ink` en claro y en `accent` en oscuro. El tema se lee del fondo. */
@Composable
private fun focusBorderColor(): Color =
  if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
    PortfolioTheme.colors.accent
  } else {
    MaterialTheme.colorScheme.onSurface
  }

private class FocusTracker {
  var focusedIndex = NoFocus
  var restoreIndex = NoFocus
  var lastIsGrid: Boolean? = null
}

/** Insets de la barra: abajo y a los lados, con barras del sistema y recorte de pantalla. Sin arriba y sin teclado. */
private val BarInsets: WindowInsets
  @Composable get() = WindowInsets.systemBars
    .union(WindowInsets.displayCutout)
    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)

/**
 * Barra inferior sin estado. Si alguna etiqueta no cabe en su columna, pasa a una grilla de 2 x 2.
 * La selección vive en quien la llama: cambiar de layout no la mueve.
 */
@Composable
fun PortfolioBottomBar(
  items: List<BottomBarItem>,
  selectedIndex: Int,
  onItemClick: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val textMeasurer = rememberTextMeasurer()
  val labelStyle = PortfolioTheme.typography.tabSelected
  val requesters = remember(items.size) { List(items.size) { FocusRequester() } }
  // Estado de foco sin observadores: solo se lee al cambiar de layout.
  val focus = remember { FocusTracker() }
  val onFocusedChange: (Int, Boolean) -> Unit = { index, focused ->
    if (focused) focus.focusedIndex = index
    else if (focus.focusedIndex == index) focus.focusedIndex = NoFocus
  }

  Column(modifier = modifier) {
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    BoxWithConstraints {
      // Ancho real de las columnas: lo que queda tras los insets horizontales que la barra
      // consume antes de repartir los ítems (la fila de ítems no lleva espacio entre ellos).
      val density = LocalDensity.current
      val layoutDirection = LocalLayoutDirection.current
      val insets = BarInsets
      val rowWidthPx = constraints.maxWidth -
        insets.getLeft(density, layoutDirection) - insets.getRight(density, layoutDirection)
      val columnWidthPx = rowWidthPx / items.size.coerceAtLeast(1)
      val availablePx = columnWidthPx - with(density) { (ColumnLabelPadding * 2).roundToPx() }
      val anyLabelOverflows = items.any { item ->
        textMeasurer.measure(
          text = item.label,
          style = labelStyle,
          overflow = TextOverflow.Clip,
          softWrap = false,
          maxLines = 1,
        ).size.width > availablePx
      }
      // Durante la composición del nuevo layout, los nodos viejos aún no se han quitado:
      // `focusedIndex` conserva el destino que tenía el foco antes del cambio.
      if (focus.lastIsGrid != anyLabelOverflows) {
        focus.restoreIndex = if (focus.lastIsGrid == null) NoFocus else focus.focusedIndex
        focus.lastIsGrid = anyLabelOverflows
      }
      LaunchedEffect(anyLabelOverflows) {
        val index = focus.restoreIndex
        if (index in requesters.indices) {
          focus.restoreIndex = NoFocus
          requesters[index].requestFocus()
        }
      }
      if (anyLabelOverflows) {
        GridBar(items, selectedIndex, onItemClick, requesters, onFocusedChange)
      } else {
        RowBar(items, selectedIndex, onItemClick, requesters, onFocusedChange)
      }
    }
  }
}

@Composable
private fun RowBar(
  items: List<BottomBarItem>,
  selectedIndex: Int,
  onItemClick: (Int) -> Unit,
  requesters: List<FocusRequester>,
  onFocusedChange: (Int, Boolean) -> Unit,
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    tonalElevation = 0.dp,
    windowInsets = BarInsets,
  ) {
    items.forEachIndexed { index, item ->
      val selected = index == selectedIndex
      var hasFocus by remember { mutableStateOf(false) }
      NavigationBarItem(
        modifier = Modifier
          .focusRequester(requesters[index])
          .onFocusChanged {
            hasFocus = it.hasFocus
            onFocusedChange(index, it.hasFocus)
          }
          .then(
            if (hasFocus) {
              Modifier.border(2.dp, focusBorderColor(), MaterialTheme.shapes.extraSmall)
            } else {
              Modifier
            },
          ),
        selected = selected,
        onClick = { onItemClick(index) },
        icon = {
          // El indicador de Material es una píldora; el diseño pide 64 x 32 dp con 2 dp de esquina.
          Box(
            modifier = Modifier
              .size(width = 64.dp, height = 32.dp)
              .clip(MaterialTheme.shapes.extraSmall)
              .background(if (selected) PortfolioTheme.colors.accent else Color.Transparent),
            contentAlignment = Alignment.Center,
          ) {
            Icon(painter = painterResource(item.icon), contentDescription = null)
          }
        },
        label = {
          Text(
            text = item.label,
            style = if (selected) {
              PortfolioTheme.typography.tabSelected
            } else {
              MaterialTheme.typography.labelMedium
            },
            maxLines = 1,
          )
        },
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = PortfolioTheme.colors.onAccent,
          selectedTextColor = MaterialTheme.colorScheme.onSurface,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
          indicatorColor = Color.Transparent,
        ),
      )
    }
  }
}

@Composable
private fun GridBar(
  items: List<BottomBarItem>,
  selectedIndex: Int,
  onItemClick: (Int) -> Unit,
  requesters: List<FocusRequester>,
  onFocusedChange: (Int, Boolean) -> Unit,
) {
  Column(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.surfaceContainer)
      .windowInsetsPadding(BarInsets)
      .padding(GridPadding)
      .selectableGroup(),
    verticalArrangement = Arrangement.spacedBy(GridPadding),
  ) {
    items.chunked(2).forEachIndexed { rowIndex, rowItems ->
      Row(horizontalArrangement = Arrangement.spacedBy(GridPadding)) {
        rowItems.forEachIndexed { columnIndex, item ->
          val index = rowIndex * 2 + columnIndex
          GridCell(
            item = item,
            selected = index == selectedIndex,
            onClick = { onItemClick(index) },
            modifier = Modifier
              .weight(1f)
              .focusRequester(requesters[index])
              .onFocusChanged { onFocusedChange(index, it.hasFocus) },
          )
        }
        if (rowItems.size == 1) Box(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun GridCell(
  item: BottomBarItem,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val contentColor =
    if (selected) PortfolioTheme.colors.onAccent else MaterialTheme.colorScheme.onSurfaceVariant
  // Capa de estado propia: solo al tocar (10 %), hover (8 %) y foco (10 %); en reposo no hay capa.
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val hovered by interactionSource.collectIsHoveredAsState()
  val focused by interactionSource.collectIsFocusedAsState()
  val layerAlpha = when {
    pressed || focused -> 0.10f
    hovered -> 0.08f
    else -> 0f
  }
  val layerColor = if (selected) PortfolioTheme.colors.onAccent else MaterialTheme.colorScheme.onSurface
  Row(
    modifier = modifier
      .heightIn(min = GridMinCellHeight)
      .clip(MaterialTheme.shapes.extraSmall)
      .background(if (selected) PortfolioTheme.colors.accent else Color.Transparent)
      .selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = null,
        role = Role.Tab,
        onClick = onClick,
      )
      .drawBehind { drawRect(layerColor.copy(alpha = layerAlpha)) }
      .then(
        if (focused) {
          Modifier.border(2.dp, focusBorderColor(), MaterialTheme.shapes.extraSmall)
        } else {
          Modifier
        },
      )
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Icon(painter = painterResource(item.icon), contentDescription = null, tint = contentColor)
    Text(
      text = item.label,
      style = if (selected) {
        PortfolioTheme.typography.tabSelected
      } else {
        MaterialTheme.typography.labelMedium
      },
      color = contentColor,
      modifier = Modifier.weight(1f, fill = false),
    )
  }
}
