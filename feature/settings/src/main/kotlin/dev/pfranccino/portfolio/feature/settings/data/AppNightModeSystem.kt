package dev.pfranccino.portfolio.feature.settings.data

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Modo de noche que el sistema guarda para la app. El splash del arranque en frío sale de ahí, así
 * que debe coincidir con el tema elegido. Las constantes son las de `UiModeManager`.
 *
 * `UiModeManager` no ofrece leer el modo de la app (solo fijarlo), así que no hay forma de comparar.
 */
internal interface AppNightModeSystem {
  /** Fija el modo de noche de la app. Sin efecto antes de Android 12. */
  fun write(nightMode: Int)
}

internal class UiModeNightModeSystem @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : AppNightModeSystem {

  override fun write(nightMode: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    context.getSystemService(UiModeManager::class.java).setApplicationNightMode(nightMode)
  }
}
