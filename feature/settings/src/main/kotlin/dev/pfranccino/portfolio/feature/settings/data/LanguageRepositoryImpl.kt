package dev.pfranccino.portfolio.feature.settings.data

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pfranccino.portfolio.feature.settings.data.mapper.languageSettingsFrom
import dev.pfranccino.portfolio.feature.settings.data.mapper.languageTagsFor
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.LanguageRepository
import dev.pfranccino.portfolio.feature.settings.domain.LanguageSettings
import javax.inject.Inject

/**
 * Idioma por app del sistema (`LocaleManager`, Android 13 o más). No guarda copia propia: lee el
 * estado en cada llamada, así que refleja los cambios hechos desde los ajustes del sistema.
 */
internal class LanguageRepositoryImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : LanguageRepository {

  override fun getLanguageSettings(): LanguageSettings {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return LanguageSettings.Unavailable
    val localeManager = context.getSystemService(LocaleManager::class.java)
    return languageSettingsFrom(
      appLanguageTags = localeManager.applicationLocales.toTags(),
      systemLanguageTags = localeManager.systemLocales.toTags(),
    )
  }

  override fun setLanguage(language: AppLanguage) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val tags = languageTagsFor(language)
    val locales = if (tags.isEmpty()) {
      LocaleList.getEmptyLocaleList()
    } else {
      LocaleList.forLanguageTags(tags.joinToString(","))
    }
    context.getSystemService(LocaleManager::class.java).applicationLocales = locales
  }

  private fun LocaleList.toTags(): List<String> =
    toLanguageTags().split(",").filter { it.isNotBlank() }
}
