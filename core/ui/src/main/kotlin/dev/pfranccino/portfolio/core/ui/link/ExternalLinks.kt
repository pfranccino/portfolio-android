package dev.pfranccino.portfolio.core.ui.link

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.core.net.toUri

/**
 * Enlaces que salen de la app (docs/PANTALLAS.md, "Enlaces externos"). Solo el tipo Custom Tab:
 * repos de GitHub, artículos de Medium y código de un capítulo.
 *
 * Tras [openInCustomTab] devuelve `false`, la pantalla muestra su aviso (texto
 * `external_link_no_app`, acción `external_link_copy`) y, si el usuario toca "Copiar", llama a
 * [copyToClipboard]. El `SnackbarHost` queda en cada pantalla.
 */
@Immutable
class ExternalLinks internal constructor(
  private val context: Context,
  private val toolbarColor: Int,
  private val darkTheme: Boolean,
) {

  /**
   * Abre [url] en una Custom Tab cuya barra usa el fondo del tema activo.
   * Devuelve `false` si nada en el teléfono puede abrirla, o si [url] no es `http` ni `https`.
   */
  fun openInCustomTab(url: String): Boolean {
    val uri = url.toUri()
    if (uri.scheme != "https" && uri.scheme != "http") return false
    val colors = CustomTabColorSchemeParams.Builder()
      .setToolbarColor(toolbarColor)
      .setNavigationBarColor(toolbarColor)
      .build()
    val customTab = CustomTabsIntent.Builder()
      .setDefaultColorSchemeParams(colors)
      .setColorScheme(
        if (darkTheme) CustomTabsIntent.COLOR_SCHEME_DARK else CustomTabsIntent.COLOR_SCHEME_LIGHT,
      )
      .build()
    // Con un contexto que no es una Activity, lanzar exige una tarea nueva.
    if (context.findActivity() == null) customTab.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    // Con el paquete del navegador, un manejador de App Links (la app de GitHub, por ejemplo) no se
    // lleva el enlace. Sin navegador con Custom Tabs, lo resuelve el sistema.
    customTabsPackage()?.let { customTab.intent.setPackage(it) }
    return try {
      customTab.launchUrl(context, uri)
      true
    } catch (_: ActivityNotFoundException) {
      false
    }
  }

  /**
   * Paquete de un navegador con Custom Tabs, o `null` si no hay ninguno. Se listan todos los que
   * ofrecen el servicio y `getPackageName` elige: el predeterminado si está en la lista y, si no,
   * el primero que sirva. En Android 11 o más la búsqueda necesita el `<queries>` de
   * `CustomTabsService` en el manifiesto.
   */
  private fun customTabsPackage(): String? {
    val serviceIntent = Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
    val services = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      context.packageManager.queryIntentServices(serviceIntent, PackageManager.ResolveInfoFlags.of(0))
    } else {
      @Suppress("DEPRECATION")
      context.packageManager.queryIntentServices(serviceIntent, 0)
    }
    val packages = services.mapNotNull { it.serviceInfo?.packageName }.distinct()
    if (packages.isEmpty()) return null
    return CustomTabsClient.getPackageName(context, packages)
  }

  /** Copia [url] al portapapeles. */
  fun copyToClipboard(url: String) {
    context.getSystemService<ClipboardManager>()
      ?.setPrimaryClip(ClipData.newPlainText(url, url))
  }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

/** Crea los enlaces externos con el fondo del tema activo como color de la barra de la Custom Tab. */
@Composable
fun rememberExternalLinks(): ExternalLinks {
  val context = LocalContext.current
  val background = MaterialTheme.colorScheme.background
  return remember(context, background) {
    ExternalLinks(
      context = context,
      toolbarColor = background.toArgb(),
      darkTheme = background.luminance() < 0.5f,
    )
  }
}
