package dev.pfranccino.portfolio.feature.settings.data

import dev.pfranccino.portfolio.feature.settings.data.mapper.languageSettingsFrom
import dev.pfranccino.portfolio.feature.settings.data.mapper.languageTagsFor
import dev.pfranccino.portfolio.feature.settings.data.mapper.selectedLanguageFrom
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.LanguageSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LanguageMapperTest {

  @Test
  fun `sin idioma propio de la app, la opción es Sistema`() {
    assertEquals(AppLanguage.System, selectedLanguageFrom(emptyList()))
  }

  @Test
  fun `la etiqueta es de la app se lee como Español, también con región`() {
    assertEquals(AppLanguage.Spanish, selectedLanguageFrom(listOf("es")))
    assertEquals(AppLanguage.Spanish, selectedLanguageFrom(listOf("es-MX")))
  }

  @Test
  fun `la etiqueta en de la app se lee como English`() {
    assertEquals(AppLanguage.English, selectedLanguageFrom(listOf("en")))
    assertEquals(AppLanguage.English, selectedLanguageFrom(listOf("en-GB")))
  }

  @Test
  fun `un idioma de la app que no es es ni en se lee como Sistema`() {
    assertEquals(AppLanguage.System, selectedLanguageFrom(listOf("fr")))
  }

  @Test
  fun `el idioma del teléfono es el primero que la app tiene`() {
    val settings = languageSettingsFrom(
      appLanguageTags = emptyList(),
      systemLanguageTags = listOf("fr-FR", "en-GB", "es-MX"),
    )

    assertEquals(
      LanguageSettings.Available(AppLanguage.System, DisplayLanguage.English, matchesPhone = true),
      settings,
    )
  }

  @Test
  fun `si el teléfono está en español, se ve en español y coincide`() {
    val settings = languageSettingsFrom(emptyList(), listOf("es-AR"))

    assertEquals(DisplayLanguage.Spanish, settings.systemDisplayLanguage)
    assertEquals(true, settings.matchesPhone)
  }

  @Test
  fun `si ningún idioma del teléfono sirve, cae a español sin coincidir`() {
    val settings = languageSettingsFrom(emptyList(), listOf("fr-FR", "de-DE"))

    assertEquals(
      LanguageSettings.Available(AppLanguage.System, DisplayLanguage.Spanish, matchesPhone = false),
      settings,
    )
  }

  @Test
  fun `sin idiomas en el teléfono cae a español sin coincidir`() {
    val settings = languageSettingsFrom(emptyList(), emptyList())

    assertEquals(DisplayLanguage.Spanish, settings.systemDisplayLanguage)
    assertEquals(false, settings.matchesPhone)
  }

  @Test
  fun `la opción elegida se refleja en el estado aunque el teléfono esté en otro idioma`() {
    val settings = languageSettingsFrom(listOf("en"), listOf("es-ES"))

    assertEquals(AppLanguage.English, settings.selected)
    assertEquals(DisplayLanguage.Spanish, settings.systemDisplayLanguage)
    assertEquals(true, settings.matchesPhone)
  }

  @Test
  fun `cada opción se escribe como su etiqueta, y Sistema como lista vacía`() {
    assertEquals(emptyList<String>(), languageTagsFor(AppLanguage.System))
    assertEquals(listOf("es"), languageTagsFor(AppLanguage.Spanish))
    assertEquals(listOf("en"), languageTagsFor(AppLanguage.English))
  }
}
