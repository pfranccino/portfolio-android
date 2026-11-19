package dev.pfranccino.portfolio.core.testing

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad vacía con soporte de Hilt para las pruebas de Compose. Permite usar
 * `createAndroidComposeRule<HiltComponentActivity>()` y `hiltViewModel()` dentro del contenido
 * que la prueba compone.
 */
@AndroidEntryPoint
class HiltComponentActivity : ComponentActivity()
