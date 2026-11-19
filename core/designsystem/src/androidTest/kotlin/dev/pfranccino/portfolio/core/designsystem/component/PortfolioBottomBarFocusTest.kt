package dev.pfranccino.portfolio.core.designsystem.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * C2: el foco de teclado queda en la misma pestaña cuando la barra pasa de la fila a la grilla de
 * 2 x 2 y vuelve. La escala de letra cambia dentro de la composición, sin recrear la actividad.
 */
@RunWith(AndroidJUnit4::class)
class PortfolioBottomBarFocusTest {

  @get:Rule
  val composeRule = createComposeRule()

  private val fontScale = mutableFloatStateOf(1f)

  private val items = listOf(
    BottomBarItem("Inicio", PortfolioIcons.Home),
    BottomBarItem("Proyectos", PortfolioIcons.Terminal),
    BottomBarItem("Artículos", PortfolioIcons.Article),
    BottomBarItem("Serie", PortfolioIcons.SmartDisplay),
  )

  @OptIn(ExperimentalComposeUiApi::class)
  @Test
  fun focusStaysOnSameTabWhenBarSwitchesToGridAndBack() {
    lateinit var inputModeManager: InputModeManager
    composeRule.setContent {
      inputModeManager = LocalInputModeManager.current
      val baseDensity = LocalDensity.current
      CompositionLocalProvider(
        LocalDensity provides Density(baseDensity.density, fontScale.floatValue),
      ) {
        PortfolioTheme(darkTheme = false) {
          PortfolioBottomBar(
            items = items,
            selectedIndex = 0,
            onItemClick = {},
            modifier = Modifier.width(360.dp),
          )
        }
      }
    }

    assertRowLayout()

    // En modo táctil, Compose no deja enfocar un ítem que no es un campo de texto.
    composeRule.runOnIdle { inputModeManager.requestInputMode(InputMode.Keyboard) }
    tab("Proyectos").performSemanticsAction(SemanticsActions.RequestFocus)
    tab("Proyectos").assertIsFocused()

    setFontScale(2f)
    assertGridLayout()
    tab("Proyectos").assertIsFocused()

    setFontScale(1f)
    assertRowLayout()
    tab("Proyectos").assertIsFocused()
  }

  private fun setFontScale(scale: Float) {
    composeRule.runOnIdle { fontScale.floatValue = scale }
    composeRule.waitForIdle()
  }

  private fun tab(label: String): SemanticsNodeInteraction = composeRule.onNodeWithText(label)

  private fun top(label: String): Float = tab(label).fetchSemanticsNode().boundsInRoot.top

  /** En la fila, todas las pestañas comparten la misma altura. */
  private fun assertRowLayout() {
    assertEquals(top("Inicio"), top("Artículos"), 0.5f)
  }

  /** En la grilla de 2 x 2, la tercera pestaña queda en la segunda fila. */
  private fun assertGridLayout() {
    assertNotEquals(top("Inicio"), top("Artículos"), 0.5f)
  }
}
