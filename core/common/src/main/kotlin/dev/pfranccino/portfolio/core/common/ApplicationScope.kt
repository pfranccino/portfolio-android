package dev.pfranccino.portfolio.core.common

import javax.inject.Qualifier

/**
 * Marca el `CoroutineScope` que vive tanto como la app (lo provee `:app`, con `SupervisorJob` y
 * `Dispatchers.IO`). Sirve para trabajo que debe sobrevivir a una pantalla, como el DataStore.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
