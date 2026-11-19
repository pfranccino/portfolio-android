package dev.pfranccino.portfolio.core.designsystem.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/**
 * Las cuatro transiciones de "Entrar a un detalle" (docs/DISENO.md, "Movimiento, con valores").
 *
 * - [enter]: el detalle entra desde [PortfolioMotion.DetailOffset] a la derecha mientras aparece,
 *   en 300 ms con `EmphasizedDecelerate`.
 * - [exit]: la pantalla anterior se desvanece en 200 ms con `EmphasizedAccelerate`, sin moverse.
 * - [popEnter]: al volver, la pantalla anterior reaparece en 300 ms con `EmphasizedDecelerate`.
 * - [popExit]: al volver, el detalle sale hacia la derecha mientras se desvanece, en 200 ms con
 *   `EmphasizedAccelerate`.
 *
 * Los 30 dp se convierten a píxeles con la [density] recibida, que se toma una vez al crear el objeto.
 * Con las animaciones del sistema apagadas, Compose escala las duraciones a cero y el cambio es inmediato.
 *
 * Uso: `val transitions = rememberPortfolioDetailTransitions()` junto al `NavHost` y
 * `composable<Destino>(enterTransition = { transitions.enter }, ...)`.
 */
@Immutable
class PortfolioDetailTransitions(density: Density) {
  private val offsetPx: Int = with(density) { PortfolioMotion.DetailOffset.roundToPx() }

  val enter: EnterTransition =
    slideInHorizontally(
      animationSpec = tween(PortfolioMotion.DetailEnterMillis, easing = PortfolioMotion.EmphasizedDecelerate),
      initialOffsetX = { offsetPx },
    ) + fadeIn(
      animationSpec = tween(PortfolioMotion.DetailEnterMillis, easing = PortfolioMotion.EmphasizedDecelerate),
    )

  val exit: ExitTransition =
    fadeOut(
      animationSpec = tween(PortfolioMotion.DetailExitMillis, easing = PortfolioMotion.EmphasizedAccelerate),
    )

  val popEnter: EnterTransition =
    fadeIn(
      animationSpec = tween(PortfolioMotion.DetailEnterMillis, easing = PortfolioMotion.EmphasizedDecelerate),
    )

  val popExit: ExitTransition =
    slideOutHorizontally(
      animationSpec = tween(PortfolioMotion.DetailExitMillis, easing = PortfolioMotion.EmphasizedAccelerate),
      targetOffsetX = { offsetPx },
    ) + fadeOut(
      animationSpec = tween(PortfolioMotion.DetailExitMillis, easing = PortfolioMotion.EmphasizedAccelerate),
    )
}

/** Crea las transiciones de detalle con la densidad actual y las recuerda mientras no cambie. */
@Composable
fun rememberPortfolioDetailTransitions(): PortfolioDetailTransitions {
  val density = LocalDensity.current
  return remember(density.density) { PortfolioDetailTransitions(density) }
}
