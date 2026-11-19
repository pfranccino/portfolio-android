package dev.pfranccino.portfolio.feature.settings.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pfranccino.portfolio.core.common.ApplicationScope
import dev.pfranccino.portfolio.feature.settings.data.AppInfoRepositoryImpl
import dev.pfranccino.portfolio.feature.settings.data.AppNightModeSystem
import dev.pfranccino.portfolio.feature.settings.data.LanguageRepositoryImpl
import dev.pfranccino.portfolio.feature.settings.data.ThemeRepositoryImpl
import dev.pfranccino.portfolio.feature.settings.data.UiModeNightModeSystem
import dev.pfranccino.portfolio.feature.settings.domain.AppInfoRepository
import dev.pfranccino.portfolio.feature.settings.domain.LanguageRepository
import dev.pfranccino.portfolio.feature.settings.domain.ThemeRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

/**
 * Enlaza los repositorios del dominio con sus implementaciones y provee el DataStore de Preferences
 * de Ajustes. El `CoroutineScope` de aplicación lo provee `:app`.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsDataModule {

  @Binds
  abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

  @Binds
  abstract fun bindLanguageRepository(impl: LanguageRepositoryImpl): LanguageRepository

  @Binds
  abstract fun bindAppInfoRepository(impl: AppInfoRepositoryImpl): AppInfoRepository

  @Binds
  abstract fun bindAppNightModeSystem(impl: UiModeNightModeSystem): AppNightModeSystem

  companion object {
    /** Una sola instancia por proceso: el archivo "settings" no puede tener dos DataStore. */
    @Provides
    @Singleton
    fun provideSettingsDataStore(
      @ApplicationContext context: Context,
      @ApplicationScope scope: CoroutineScope,
    ): DataStore<Preferences> =
      PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        scope = scope,
        produceFile = { context.preferencesDataStoreFile("settings") },
      )
  }
}
