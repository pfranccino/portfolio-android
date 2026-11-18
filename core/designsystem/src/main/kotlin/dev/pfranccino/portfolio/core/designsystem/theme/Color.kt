package dev.pfranccino.portfolio.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Tokens del diseño (docs/DISENO.md, sección Color). */
private object Tokens {
  val bgLight = Color(0xFFEDEFF0)
  val bgDark = Color(0xFF121416)
  val bg2Light = Color(0xFFE1E4E6)
  val bg2Dark = Color(0xFF1A1D20)
  val surfaceLight = Color(0xFFF7F8F8)
  val surfaceDark = Color(0xFF202428)
  val inkLight = Color(0xFF15181B)
  val inkDark = Color(0xFFE8EAEC)
  val ink2Light = Color(0xFF3F454B)
  val ink2Dark = Color(0xFFB4BAC0)
  val ruleLight = Color(0xFFC5CACF)
  val ruleDark = Color(0xFF2E3338)
  val accent = Color(0xFFF2C230)
  val onAccent = Color(0xFF15181B)
  val onAccent2 = Color(0xFF4A3F12)
  val okLight = Color(0xFF1F7A45)
  val okDark = Color(0xFF4CC27A)
  val scrimLight = Color(0xFF15181B).copy(alpha = 0.52f)
  val scrimDark = Color(0xFF000000).copy(alpha = 0.60f)
}

/** Colores sin rol propio en Material 3, y los de la terminal. */
@Immutable
class PortfolioColors(
  val accent: Color,
  val onAccent: Color,
  val onAccent2: Color,
  val ok: Color,
  val scrim: Color,
  val codeBg: Color,
  val codeInk: Color,
  val codeDim: Color,
  val codeOk: Color,
  val codeWarn: Color,
  val codeErr: Color,
  val codeKey: Color,
  /** Texto de metadatos y chevrones (`ink3`). */
  val ink3: Color,
)

internal val LightPortfolioColors = PortfolioColors(
  accent = Tokens.accent,
  onAccent = Tokens.onAccent,
  onAccent2 = Tokens.onAccent2,
  ok = Tokens.okLight,
  scrim = Tokens.scrimLight,
  codeBg = Color(0xFF15181B),
  codeInk = Color(0xFFE8EAEC),
  codeDim = Color(0xFF9AA1A8),
  codeOk = Color(0xFF7FD49A),
  codeWarn = Color(0xFFF2C230),
  codeErr = Color(0xFFFF8A7A),
  codeKey = Color(0xFF8DB8FF),
  ink3 = Color(0xFF575E65),
)

internal val DarkPortfolioColors = PortfolioColors(
  accent = Tokens.accent,
  onAccent = Tokens.onAccent,
  onAccent2 = Tokens.onAccent2,
  ok = Tokens.okDark,
  scrim = Tokens.scrimDark,
  codeBg = Color(0xFF0B0D0E),
  codeInk = Color(0xFFE8EAEC),
  codeDim = Color(0xFF9AA1A8),
  codeOk = Color(0xFF7FD49A),
  codeWarn = Color(0xFFF2C230),
  codeErr = Color(0xFFFF8A7A),
  codeKey = Color(0xFF8DB8FF),
  ink3 = Color(0xFF8F969D),
)

internal val LightColorScheme: ColorScheme = lightColorScheme(
  background = Tokens.bgLight,
  surface = Tokens.bgLight,
  surfaceContainer = Tokens.surfaceLight,
  surfaceContainerLow = Tokens.surfaceLight,
  surfaceContainerHighest = Tokens.bg2Light,
  surfaceVariant = Tokens.bg2Light,
  onBackground = Tokens.inkLight,
  onSurface = Tokens.inkLight,
  onSurfaceVariant = Tokens.ink2Light,
  primary = Tokens.inkLight,
  onPrimary = Tokens.bgLight,
  secondaryContainer = Tokens.accent,
  onSecondaryContainer = Tokens.onAccent,
  outline = Tokens.ink2Light,
  outlineVariant = Tokens.ruleLight,
  inverseSurface = Tokens.inkLight,
  inverseOnSurface = Tokens.bgLight,
  inversePrimary = Tokens.accent,
  scrim = Tokens.scrimLight,
)

internal val DarkColorScheme: ColorScheme = darkColorScheme(
  background = Tokens.bgDark,
  surface = Tokens.bgDark,
  surfaceContainer = Tokens.surfaceDark,
  surfaceContainerLow = Tokens.surfaceDark,
  surfaceContainerHighest = Tokens.bg2Dark,
  surfaceVariant = Tokens.bg2Dark,
  onBackground = Tokens.inkDark,
  onSurface = Tokens.inkDark,
  onSurfaceVariant = Tokens.ink2Dark,
  primary = Tokens.inkDark,
  onPrimary = Tokens.bgDark,
  secondaryContainer = Tokens.accent,
  onSecondaryContainer = Tokens.onAccent,
  outline = Tokens.ink2Dark,
  outlineVariant = Tokens.ruleDark,
  inverseSurface = Tokens.inkDark,
  inverseOnSurface = Tokens.bgDark,
  inversePrimary = Tokens.bgDark,
  scrim = Tokens.scrimDark,
)
