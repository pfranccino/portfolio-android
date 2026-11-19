package dev.pfranccino.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pfranccino.portfolio.feature.settings.domain.ObserveThemeModeUseCase
import dev.pfranccino.portfolio.feature.settings.domain.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado de arranque: carga hasta que llega el primer modo de tema. */
sealed interface MainUiState {
  data object Loading : MainUiState

  data class Ready(val themeMode: ThemeMode) : MainUiState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  observeThemeMode: ObserveThemeModeUseCase,
) : ViewModel() {
  val uiState: StateFlow<MainUiState> = observeThemeMode()
    .map<ThemeMode, MainUiState> { MainUiState.Ready(it) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = MainUiState.Loading,
    )
}
