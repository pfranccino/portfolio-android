package dev.pfranccino.portfolio.feature.settings.domain

import dev.pfranccino.portfolio.core.common.Either
import kotlinx.coroutines.flow.Flow

/** Preferencia de tema guardada en el dispositivo. */
internal interface ThemeRepository {
  /** Emite el modo guardado; [ThemeMode.System] si no hay ninguno o si falla la lectura. */
  fun observeThemeMode(): Flow<ThemeMode>

  suspend fun setThemeMode(mode: ThemeMode): Either<SettingsError, Unit>
}

/** Idioma por app del sistema. La app no guarda una copia propia. */
internal interface LanguageRepository {
  /** Lee el estado actual del sistema en cada llamada. */
  fun getLanguageSettings(): LanguageSettings

  /** Aplica el idioma al instante. Sin efecto si el sistema no ofrece idioma por app. */
  fun setLanguage(language: AppLanguage)
}

/** Datos de la app instalada. */
internal interface AppInfoRepository {
  /** El nombre de versión, por ejemplo "1.0". */
  fun getVersionName(): String
}
