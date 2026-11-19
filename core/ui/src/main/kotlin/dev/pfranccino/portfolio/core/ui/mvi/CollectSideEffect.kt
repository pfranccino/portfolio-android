package dev.pfranccino.portfolio.core.ui.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Recoge los efectos de una sola vez mientras el ciclo de vida está al menos en [minActiveState].
 * Si la pantalla sale de ese estado, deja de recoger y los efectos esperan en el canal.
 *
 * Uso: `CollectSideEffect(viewModel.sideEffect) { effect -> when (effect) { ... } }`.
 */
@Composable
fun <E> CollectSideEffect(
  sideEffect: Flow<E>,
  lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
  onEffect: (E) -> Unit,
) {
  val currentOnEffect by rememberUpdatedState(onEffect)
  LaunchedEffect(sideEffect, lifecycleOwner, minActiveState) {
    lifecycleOwner.repeatOnLifecycle(minActiveState) {
      sideEffect.collect { currentOnEffect(it) }
    }
  }
}
