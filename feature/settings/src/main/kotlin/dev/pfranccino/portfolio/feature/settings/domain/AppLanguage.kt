package dev.pfranccino.portfolio.feature.settings.domain

/** Idioma que se puede elegir para la app. [System] significa que la app sigue al teléfono. */
internal enum class AppLanguage { System, Spanish, English }

/** Idioma en el que la app se ve de verdad. */
internal enum class DisplayLanguage { Spanish, English }

/**
 * Estado del selector de idioma.
 *
 * [Unavailable] cuando el sistema no ofrece idioma por app (antes de Android 13).
 */
internal sealed interface LanguageSettings {
  data object Unavailable : LanguageSettings

  /**
   * @property selected la opción que tiene la app ahora, leída del sistema.
   * @property systemDisplayLanguage el idioma en que se ve la app cuando sigue al teléfono.
   * @property matchesPhone si [systemDisplayLanguage] es también el idioma del teléfono; es falso
   * cuando el teléfono está en un idioma que la app no tiene y cae al español.
   */
  data class Available(
    val selected: AppLanguage,
    val systemDisplayLanguage: DisplayLanguage,
    val matchesPhone: Boolean,
  ) : LanguageSettings
}
