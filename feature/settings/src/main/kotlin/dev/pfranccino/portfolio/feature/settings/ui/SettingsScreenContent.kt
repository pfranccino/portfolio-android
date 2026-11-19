package dev.pfranccino.portfolio.feature.settings.ui

import android.app.UiModeManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.pfranccino.portfolio.core.designsystem.component.PortfolioTopAppBar
import dev.pfranccino.portfolio.core.designsystem.component.portfolioClickableRow
import dev.pfranccino.portfolio.core.designsystem.component.portfolioSelectableRow
import dev.pfranccino.portfolio.core.designsystem.icon.PortfolioIcons
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioSpacing
import dev.pfranccino.portfolio.core.designsystem.theme.PortfolioTheme
import dev.pfranccino.portfolio.feature.settings.R
import dev.pfranccino.portfolio.feature.settings.domain.AppLanguage
import dev.pfranccino.portfolio.feature.settings.domain.DisplayLanguage
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract.UiAction

/** Alto mínimo de una fila de opción o de "Acerca de". */
private val RowMinHeight = 56.dp

/**
 * Con `onClick = null`, el `RadioButton` de Material 3 no agrega el área táctil de 48 dp: mide el
 * círculo de 20 dp más 2 dp de relleno por lado, 24 dp en total. Para que el borde izquierdo del
 * círculo caiga en el margen de pantalla, la fila se corre esos 2 dp.
 */
private val RadioInternalPadding = 2.dp

/** Distancia del borde izquierdo del círculo al inicio de la etiqueta, como en las maquetas. */
private val RadioToLabelOffset = 36.dp

/** Alto del `RadioButton` sin área táctil: círculo de 20 dp y relleno de 2 dp por lado. */
private val RadioBoxSize = 24.dp

/** Ajustes sin estado: recibe el estado y reporta las acciones. No carga nada por sí sola. */
@Composable
internal fun SettingsScreenContent(
  state: SettingsContract.UiState,
  modifier: Modifier = Modifier,
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
  onBack: () -> Unit = {},
  onAction: (UiAction) -> Unit = {},
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      PortfolioTopAppBar(
        title = stringResource(R.string.feature_settings_title),
        onBackClick = onBack,
        backContentDescription = stringResource(R.string.feature_settings_back),
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = MaterialTheme.colorScheme.background,
    // La barra superior resuelve los insets de arriba; aquí van los de abajo y los costados,
    // y el aviso queda por encima de la barra del sistema.
    contentWindowInsets = WindowInsets.safeDrawing.only(
      WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
    ),
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(bottom = PortfolioSpacing.Space32),
    ) {
      state.language?.let { language ->
        LanguageGroup(language = language, onAction = onAction)
      }
      ThemeGroup(selected = state.themeMode, onAction = onAction)
      AboutGroup(version = state.version, onSourceCodeClick = { onAction(UiAction.OnSourceCodeClick) })
    }
  }
}

@Composable
private fun LanguageGroup(
  language: SettingsContract.LanguageUi,
  onAction: (UiAction) -> Unit,
) {
  val systemName = stringResource(
    when (language.systemDisplayLanguage) {
      DisplayLanguage.Spanish -> R.string.feature_settings_language_name_spanish
      DisplayLanguage.English -> R.string.feature_settings_language_name_english
    },
  )
  // Si el teléfono está en un idioma que la app no tiene, solo se dice el idioma en que se ve.
  val systemSubtitle = if (language.matchesPhone) {
    stringResource(R.string.feature_settings_like_phone, systemName)
  } else {
    systemName
  }
  OptionGroup(title = stringResource(R.string.feature_settings_language_title)) {
    OptionRow(
      title = stringResource(R.string.feature_settings_option_system),
      subtitle = systemSubtitle,
      selected = language.selected == AppLanguage.System,
      onClick = { onAction(UiAction.OnLanguageClick(AppLanguage.System)) },
    )
    OptionRow(
      title = stringResource(R.string.feature_settings_language_name_spanish),
      selected = language.selected == AppLanguage.Spanish,
      onClick = { onAction(UiAction.OnLanguageClick(AppLanguage.Spanish)) },
    )
    OptionRow(
      title = stringResource(R.string.feature_settings_language_name_english),
      selected = language.selected == AppLanguage.English,
      onClick = { onAction(UiAction.OnLanguageClick(AppLanguage.English)) },
    )
  }
}

/**
 * Modo real del teléfono. En Android 12 o más la app fija su propio modo de noche, así que su
 * configuración (y la global del proceso) sigue al tema elegido y no sirve. Se lee el ajuste global
 * de `UiModeManager.nightMode`: `YES` es oscuro y `NO` es claro. Con `AUTO` o `CUSTOM` hay un
 * horario que la app no puede evaluar y se usa la configuración de la app: es exacta con el tema
 * "Sistema" y una aproximación si el usuario eligió Claro u Oscuro.
 */
@Composable
private fun phoneIsDark(): Boolean {
  val context = LocalContext.current
  val nightMode = context.getSystemService(UiModeManager::class.java)?.nightMode
  val appIsDark = isSystemInDarkTheme()
  return when (nightMode) {
    UiModeManager.MODE_NIGHT_YES -> true
    UiModeManager.MODE_NIGHT_NO -> false
    else -> appIsDark
  }
}

@Composable
private fun ThemeGroup(
  selected: ThemeMode,
  onAction: (UiAction) -> Unit,
) {
  val phoneModeName = stringResource(
    if (phoneIsDark()) {
      R.string.feature_settings_option_dark
    } else {
      R.string.feature_settings_option_light
    },
  )
  OptionGroup(title = stringResource(R.string.feature_settings_theme_title)) {
    OptionRow(
      title = stringResource(R.string.feature_settings_option_system),
      subtitle = stringResource(R.string.feature_settings_like_phone, phoneModeName),
      selected = selected == ThemeMode.System,
      onClick = { onAction(UiAction.OnThemeClick(ThemeMode.System)) },
    )
    OptionRow(
      title = stringResource(R.string.feature_settings_option_light),
      selected = selected == ThemeMode.Light,
      onClick = { onAction(UiAction.OnThemeClick(ThemeMode.Light)) },
    )
    OptionRow(
      title = stringResource(R.string.feature_settings_option_dark),
      selected = selected == ThemeMode.Dark,
      onClick = { onAction(UiAction.OnThemeClick(ThemeMode.Dark)) },
    )
  }
}

@Composable
private fun AboutGroup(
  version: String,
  onSourceCodeClick: () -> Unit,
) {
  Column {
    GroupTitle(text = stringResource(R.string.feature_settings_about_title))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = RowMinHeight)
        .semantics(mergeDescendants = true) {}
        .padding(horizontal = PortfolioSpacing.ScreenMargin, vertical = PortfolioSpacing.Space8),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(PortfolioSpacing.Space16),
    ) {
      Text(
        text = stringResource(R.string.feature_settings_version),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.weight(1f),
      )
      Text(
        text = version,
        style = MaterialTheme.typography.labelSmall,
        color = PortfolioTheme.colors.ink3,
      )
    }
    RowDivider()
    val sourceCodeDescription = stringResource(R.string.feature_settings_source_code_description)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = RowMinHeight)
        .portfolioClickableRow(onClick = onSourceCodeClick, role = Role.Button)
        .clearAndSetSemantics { contentDescription = sourceCodeDescription }
        .padding(horizontal = PortfolioSpacing.ScreenMargin, vertical = PortfolioSpacing.Space8),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(PortfolioSpacing.Space16),
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.feature_settings_source_code),
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = stringResource(R.string.feature_settings_source_code_url),
          style = MaterialTheme.typography.labelSmall,
          color = PortfolioTheme.colors.ink3,
        )
      }
      Icon(
        painter = painterResource(PortfolioIcons.ArrowOutward),
        contentDescription = null,
        tint = PortfolioTheme.colors.ink3,
      )
    }
    RowDivider()
  }
}

@Composable
private fun RowDivider() {
  HorizontalDivider(
    modifier = Modifier.padding(horizontal = PortfolioSpacing.ScreenMargin),
    thickness = 1.dp,
    color = MaterialTheme.colorScheme.outlineVariant,
  )
}

@Composable
private fun GroupTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier
      .fillMaxWidth()
      .padding(
        start = PortfolioSpacing.ScreenMargin,
        end = PortfolioSpacing.ScreenMargin,
        top = PortfolioSpacing.Space24,
        bottom = PortfolioSpacing.Space4,
      )
      .semantics { heading() },
  )
}

/** Grupo de opciones excluyentes: título como encabezado y las filas como un solo grupo. */
@Composable
private fun OptionGroup(
  title: String,
  content: @Composable () -> Unit,
) {
  Column(modifier = Modifier.selectableGroup()) {
    GroupTitle(text = title)
    content()
  }
}

/** Fila de opción: toda ella es seleccionable; el `RadioButton` de adentro solo dibuja. */
@Composable
private fun OptionRow(
  title: String,
  selected: Boolean,
  onClick: () -> Unit,
  subtitle: String? = null,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = RowMinHeight)
      .portfolioSelectableRow(selected = selected, onClick = onClick, role = Role.RadioButton)
      .padding(
        start = PortfolioSpacing.ScreenMargin - RadioInternalPadding,
        end = PortfolioSpacing.ScreenMargin,
        top = PortfolioSpacing.Space4,
        bottom = PortfolioSpacing.Space4,
      ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(RadioToLabelOffset - RadioInternalPadding - RadioBoxSize),
  ) {
    RadioButton(
      selected = selected,
      onClick = null,
      colors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.onBackground,
        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
      ),
    )
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = PortfolioTheme.colors.ink3,
        )
      }
    }
  }
}
