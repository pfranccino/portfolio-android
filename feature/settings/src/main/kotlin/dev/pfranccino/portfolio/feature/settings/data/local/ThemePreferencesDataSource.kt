package dev.pfranccino.portfolio.feature.settings.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import javax.inject.Inject

/** Lo que devuelve una lectura: el valor guardado, o que el almacén no se pudo leer. */
internal sealed interface StoredTheme {
  /** [value] es `null` si nunca se guardó un tema. */
  data class Saved(val value: String?) : StoredTheme

  /** Falló la lectura; no hay que tomar el tema guardado como cierto. */
  data object Unreadable : StoredTheme
}

/** Lee y escribe el tema en DataStore. Guarda texto; la traducción al dominio está en el repositorio. */
internal class ThemePreferencesDataSource @Inject constructor(
  private val dataStore: DataStore<Preferences>,
) {
  /**
   * La lectura del tema. Si falla con [IOException], emite [StoredTheme.Unreadable] y reintenta hasta
   * [MAX_RETRIES] veces, con una pausa que crece. Tras el último fallo queda en [StoredTheme.Unreadable]
   * y termina sin lanzar. Otros errores se relanzan.
   */
  val storedTheme: Flow<StoredTheme> = dataStore.data
    .map<Preferences, StoredTheme> { preferences -> StoredTheme.Saved(preferences[THEME_MODE]) }
    .retryWhen { cause, attempt ->
      if (cause is IOException && attempt < MAX_RETRIES) {
        emit(StoredTheme.Unreadable)
        delay(RETRY_DELAY_MS * (attempt + 1))
        true
      } else {
        false
      }
    }
    .catch { cause ->
      if (cause is IOException) emit(StoredTheme.Unreadable) else throw cause
    }

  /** Guarda el valor. Un [IOException] de escritura llega a quien llama. */
  suspend fun saveThemeValue(value: String) {
    dataStore.edit { preferences -> preferences[THEME_MODE] = value }
  }

  private companion object {
    // El nombre de la clave es permanente: cambiarlo pierde el tema guardado.
    val THEME_MODE = stringPreferencesKey("theme_mode")
    const val MAX_RETRIES = 3L
    const val RETRY_DELAY_MS = 500L
  }
}
