package dev.pfranccino.portfolio.feature.settings.domain

import dev.pfranccino.portfolio.core.common.Either
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observa el modo de tema. Es público porque `:app` lo aplica en `MainActivity`; el constructor
 * es `internal` para no exponer el repositorio.
 */
class ObserveThemeModeUseCase @Inject internal constructor(
  private val repository: ThemeRepository,
) {
  operator fun invoke(): Flow<ThemeMode> = repository.observeThemeMode()
}

internal class SetThemeModeUseCase @Inject constructor(
  private val repository: ThemeRepository,
) {
  suspend operator fun invoke(mode: ThemeMode): Either<SettingsError, Unit> =
    repository.setThemeMode(mode)
}

internal class GetLanguageSettingsUseCase @Inject constructor(
  private val repository: LanguageRepository,
) {
  operator fun invoke(): LanguageSettings = repository.getLanguageSettings()
}

internal class SetLanguageUseCase @Inject constructor(
  private val repository: LanguageRepository,
) {
  operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}

internal class GetAppVersionUseCase @Inject constructor(
  private val repository: AppInfoRepository,
) {
  operator fun invoke(): String = repository.getVersionName()
}
