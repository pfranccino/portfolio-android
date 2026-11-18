package dev.pfranccino.portfolio.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.pfranccino.portfolio.core.designsystem.R

@OptIn(ExperimentalTextApi::class)
private fun archivo(weight: Int, width: Float): FontFamily = FontFamily(
  Font(
    resId = R.font.archivo_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(
      FontVariation.weight(weight),
      FontVariation.width(width),
    ),
  ),
)

@OptIn(ExperimentalTextApi::class)
private fun mono(weight: Int): FontFamily = FontFamily(
  Font(
    resId = R.font.jetbrains_mono_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
  ),
)

private fun archivoStyle(size: Int, line: Int, weight: Int, width: Float) = TextStyle(
  fontFamily = archivo(weight, width),
  fontWeight = FontWeight(weight),
  fontSize = size.sp,
  lineHeight = line.sp,
  letterSpacing = 0.sp,
)

private fun monoStyle(size: Int, line: Int, weight: Int) = TextStyle(
  fontFamily = mono(weight),
  fontWeight = FontWeight(weight),
  fontSize = size.sp,
  lineHeight = line.sp,
  letterSpacing = 0.sp,
)

/** Roles propios que Material 3 no tiene. */
@Immutable
class PortfolioTypography(
  /** Entrada: frase bajo el título. */
  val lead: TextStyle,
  /** Pregunta de cada grupo de proyectos. */
  val question: TextStyle,
  /** Cifra: +7, 4, 6. */
  val figure: TextStyle,
  /** Repo: nombre de repo en una fila. */
  val repo: TextStyle,
  /** Repo grande: nombre de repo en el detalle. */
  val repoLarge: TextStyle,
  /** Código: terminal y comandos. */
  val code: TextStyle,
  /** Pestaña activa: la etiqueta `labelMedium` en negrita. */
  val tabSelected: TextStyle,
)

internal val PortfolioMaterialTypography = Typography(
  displaySmall = archivoStyle(34, 36, 800, 118f),
  headlineMedium = archivoStyle(30, 32, 780, 116f),
  titleLarge = archivoStyle(20, 24, 720, 110f),
  titleMedium = archivoStyle(18, 24, 720, 106f),
  titleSmall = archivoStyle(17, 22, 700, 104f),
  bodyLarge = archivoStyle(16, 24, 400, 100f),
  bodyMedium = archivoStyle(14, 20, 400, 100f),
  labelLarge = archivoStyle(16, 20, 700, 108f),
  labelMedium = archivoStyle(12, 16, 500, 100f),
  labelSmall = monoStyle(12, 17, 500),
)

internal val DefaultPortfolioTypography = PortfolioTypography(
  lead = archivoStyle(18, 24, 500, 104f),
  question = archivoStyle(15, 21, 400, 100f),
  figure = archivoStyle(32, 32, 800, 118f),
  repo = monoStyle(15, 20, 600),
  repoLarge = monoStyle(25, 29, 700),
  code = monoStyle(12, 20, 400),
  tabSelected = archivoStyle(12, 16, 700, 100f),
)
