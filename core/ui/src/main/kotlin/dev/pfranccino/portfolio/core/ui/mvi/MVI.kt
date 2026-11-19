package dev.pfranccino.portfolio.core.ui.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Contrato MVI de una pantalla: estado [S], acciones [A] y efectos de una sola vez [E].
 *
 * Un `ViewModel` lo implementa por delegación:
 * `class XViewModel(...) : ViewModel(), MVI<S, A, E> by mvi(S())`.
 * El estado y los efectos pasan solo por el delegado; el `ViewModel` no crea sus propios flujos.
 * El delegado no arranca ningún trabajo: la primera carga la dispara la pantalla con una acción `OnStart`.
 */
interface MVI<S, A, E> {
  /** Estado actual de la pantalla. */
  val uiState: StateFlow<S>

  /** Efectos de una sola vez. Cada efecto llega a un único colector; si no hay ninguno, espera. */
  val sideEffect: Flow<E>

  /** Recibe lo que pasa en la pantalla. El `ViewModel` lo sobrescribe con un `when` exhaustivo. */
  fun onAction(action: A)

  /** Cambia el estado de forma atómica: `updateUiState { copy(isLoading = false) }`. */
  fun updateUiState(reducer: S.() -> S)

  /**
   * Emite un efecto desde [this] scope, normalmente `viewModelScope` o el bloque de un `launch`.
   * Los efectos conservan el orden en que se emiten.
   */
  fun CoroutineScope.emitSideEffect(effect: E)
}

/**
 * Crea el delegado del contrato [MVI] con [initialState]. Su [MVI.onAction] no hace nada:
 * el `ViewModel` lo sobrescribe.
 */
fun <S, A, E> mvi(initialState: S): MVI<S, A, E> = MviDelegate(initialState)

private class MviDelegate<S, A, E>(initialState: S) : MVI<S, A, E> {
  private val state = MutableStateFlow(initialState)
  private val effects = Channel<E>(Channel.BUFFERED)

  override val uiState: StateFlow<S> = state.asStateFlow()
  override val sideEffect: Flow<E> = effects.receiveAsFlow()

  override fun onAction(action: A) = Unit

  override fun updateUiState(reducer: S.() -> S) {
    state.update { it.reducer() }
  }

  override fun CoroutineScope.emitSideEffect(effect: E) {
    // UNDISPATCHED: el envío corre ya, sin esperar un despacho, y por eso conserva el orden.
    launch(start = CoroutineStart.UNDISPATCHED) { effects.send(effect) }
  }
}
