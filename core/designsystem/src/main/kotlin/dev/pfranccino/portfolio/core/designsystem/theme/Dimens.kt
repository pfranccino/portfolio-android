package dev.pfranccino.portfolio.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val Corner = RoundedCornerShape(2.dp)

/** Todas las formas de Material 3 valen 2 dp. */
internal val PortfolioShapes = Shapes(
  extraSmall = Corner,
  small = Corner,
  medium = Corner,
  large = Corner,
  extraLarge = Corner,
)

/** Espacio: margen lateral de 20 dp y escala vertical de 4 en 4. */
object PortfolioSpacing {
  val ScreenMargin = 20.dp
  val Space4 = 4.dp
  val Space8 = 8.dp
  val Space12 = 12.dp
  val Space16 = 16.dp
  val Space20 = 20.dp
  val Space24 = 24.dp
  val Space28 = 28.dp
  val Space32 = 32.dp
}

/** Movimiento (docs/DISENO.md, "Movimiento, con valores"). */
object PortfolioMotion {
  val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
  val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

  /** Cambio de pestaña: la pantalla que sale se desvanece en 100 ms. */
  const val TabExitMillis = 100

  /** Cambio de pestaña: la pantalla que entra aparece en 200 ms. */
  const val TabEnterMillis = 200
}
