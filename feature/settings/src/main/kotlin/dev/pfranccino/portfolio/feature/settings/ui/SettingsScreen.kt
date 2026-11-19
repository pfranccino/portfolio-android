package dev.pfranccino.portfolio.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pfranccino.portfolio.core.ui.link.rememberExternalLinks
import dev.pfranccino.portfolio.core.ui.mvi.CollectSideEffect
import dev.pfranccino.portfolio.feature.settings.R
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsContract
import dev.pfranccino.portfolio.feature.settings.presentation.SettingsViewModel
import kotlinx.coroutines.launch
import dev.pfranccino.portfolio.core.ui.R as CoreUiR

/**
 * Ajustes con su estado. [onBack] es la única salida: la flecha de la barra y el atrás del
 * sistema la llaman desde el grafo.
 */
@Composable
internal fun SettingsScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val externalLinks = rememberExternalLinks()
  val themeSaveError = stringResource(R.string.feature_settings_theme_save_error)
  val noAppMessage = stringResource(CoreUiR.string.external_link_no_app)
  val copyLabel = stringResource(CoreUiR.string.external_link_copy)

  LaunchedEffect(viewModel) {
    viewModel.onAction(SettingsContract.UiAction.OnStart)
  }

  CollectSideEffect(viewModel.sideEffect) { effect ->
    when (effect) {
      SettingsContract.SideEffect.ThemeSaveFailed -> scope.launch {
        snackbarHostState.showSnackbar(message = themeSaveError)
      }

      is SettingsContract.SideEffect.OpenLink -> {
        if (!externalLinks.openInCustomTab(effect.url)) {
          scope.launch {
            val result = snackbarHostState.showSnackbar(
              message = noAppMessage,
              actionLabel = copyLabel,
              duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) externalLinks.copyToClipboard(effect.url)
          }
        }
      }
    }
  }

  SettingsScreenContent(
    state = state,
    modifier = modifier,
    snackbarHostState = snackbarHostState,
    onBack = onBack,
    onAction = viewModel::onAction,
  )
}
