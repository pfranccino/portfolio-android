package dev.pfranccino.portfolio.core.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Runner de las pruebas instrumentadas que usan Hilt. Reemplaza la aplicación real por
 * [HiltTestApplication], para que cada prueba arme su propio grafo de dependencias.
 *
 * Se configura en el módulo que lo necesita con
 * `defaultConfig.testInstrumentationRunner = "dev.pfranccino.portfolio.core.testing.HiltTestRunner"`.
 */
class HiltTestRunner : AndroidJUnitRunner() {
  override fun newApplication(
    classLoader: ClassLoader?,
    className: String?,
    context: Context?,
  ): Application = super.newApplication(classLoader, HiltTestApplication::class.java.name, context)
}
