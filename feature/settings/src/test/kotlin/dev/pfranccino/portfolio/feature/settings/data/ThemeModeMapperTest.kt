package dev.pfranccino.portfolio.feature.settings.data

import android.app.UiModeManager
import dev.pfranccino.portfolio.feature.settings.data.mapper.toNightMode
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThemeModeMapperTest {

  @Test
  fun `Sistema sigue al teléfono con el modo automático de la app`() {
    assertEquals(UiModeManager.MODE_NIGHT_AUTO, ThemeMode.System.toNightMode())
  }

  @Test
  fun `Claro y Oscuro fijan el modo de noche de la app`() {
    assertEquals(UiModeManager.MODE_NIGHT_NO, ThemeMode.Light.toNightMode())
    assertEquals(UiModeManager.MODE_NIGHT_YES, ThemeMode.Dark.toNightMode())
  }
}
