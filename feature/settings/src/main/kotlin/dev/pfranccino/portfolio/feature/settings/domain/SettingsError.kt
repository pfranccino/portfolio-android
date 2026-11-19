package dev.pfranccino.portfolio.feature.settings.domain

/** Lo que puede fallar en Ajustes, en términos del negocio. */
internal sealed interface SettingsError {
  /** No se pudo guardar la preferencia. */
  data object CouldNotSave : SettingsError
}
