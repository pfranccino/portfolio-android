package dev.pfranccino.portfolio.feature.settings.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pfranccino.portfolio.core.common.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * El `@ApplicationScope` lo provee `:app` (`CoroutinesModule`), que las pruebas de este módulo no
 * incluyen. Aquí se repite lo mismo para que el grafo de Hilt de las pruebas cierre.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object TestCoroutinesModule {
  @Provides
  @Singleton
  @ApplicationScope
  fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
