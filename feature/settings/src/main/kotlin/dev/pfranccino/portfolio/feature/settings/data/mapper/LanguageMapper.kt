package dev.pfranccino.portfolio.feature.settings.data.mapper

import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.LanguageSettings
import java.util.Locale

// Funciones puras: reciben etiquetas BCP 47 ("es", "en-GB") y devuelven valores del dominio.
// No usan clases de Android, así que se prueban en la JVM.

/** La opción que tiene la app: la primera etiqueta de sus idiomas. Sin idiomas o no soportada, Sistema. */
internal fun selectedLanguageFrom(appLanguageTags: List<String>): AppLanguage =
  when (languageOf(appLanguageTags.firstOrNull())) {
    "es" -> AppLanguage.Spanish
    "en" -> AppLanguage.English
    else -> AppLanguage.System
  }

/**
 * Estado del selector. El idioma en que se ve la app sale de la primera etiqueta del teléfono que
 * la app tiene (es o en), como lo elige Android para sus recursos. Si ninguna sirve, la app cae a
 * español y [LanguageSettings.Available.matchesPhone] es falso.
 */
internal fun languageSettingsFrom(
  appLanguageTags: List<String>,
  systemLanguageTags: List<String>,
): LanguageSettings.Available {
  val phoneLanguage = systemLanguageTags.firstNotNullOfOrNull { displayLanguageOf(it) }
  return LanguageSettings.Available(
    selected = selectedLanguageFrom(appLanguageTags),
    systemDisplayLanguage = phoneLanguage ?: DisplayLanguage.Spanish,
    matchesPhone = phoneLanguage != null,
  )
}

/** Etiquetas que se escriben en el sistema. Lista vacía significa Sistema. */
internal fun languageTagsFor(language: AppLanguage): List<String> = when (language) {
  AppLanguage.System -> emptyList()
  AppLanguage.Spanish -> listOf("es")
  AppLanguage.English -> listOf("en")
}

private fun displayLanguageOf(tag: String): DisplayLanguage? = when (languageOf(tag)) {
  "es" -> DisplayLanguage.Spanish
  "en" -> DisplayLanguage.English
  else -> null
}

/** Código de idioma de la etiqueta ("es" de "es-MX"), o vacío si no hay etiqueta. */
private fun languageOf(tag: String?): String =
  tag?.let { Locale.forLanguageTag(it).language }.orEmpty()
