package dev.pfranccino.portfolio.core.ui.mvi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MviTest {

  private data class State(val count: Int = 0, val label: String = "")

  private sealed interface Action {
    data object OnIncrement : Action
    data object OnDone : Action
  }

  private sealed interface Effect {
    data class Message(val text: String) : Effect
  }

  /** Imita a un `ViewModel`: usa el delegado y sobrescribe `onAction`. */
  private class Host : MVI<State, Action, Effect> by mvi(State()) {
    override fun onAction(action: Action) = when (action) {
      Action.OnIncrement -> updateUiState { copy(count = count + 1) }
      Action.OnDone -> updateUiState { copy(label = "done") }
    }
  }

  @Test
  fun `state starts with the initial value`() {
    assertEquals(State(), Host().uiState.value)
  }

  @Test
  fun `updateUiState applies the reducer to the current state`() {
    val host = Host()

    host.onAction(Action.OnIncrement)
    host.onAction(Action.OnIncrement)
    host.onAction(Action.OnDone)

    assertEquals(State(count = 2, label = "done"), host.uiState.value)
  }

  @Test
  fun `effect is delivered once to a collector`() = runTest {
    val host = Host()
    val received = mutableListOf<Effect>()
    val job = launch { host.sideEffect.collect { received += it } }
    advanceUntilIdle()

    with(host) { emitSideEffect(Effect.Message("hola")) }
    advanceUntilIdle()

    assertEquals(listOf<Effect>(Effect.Message("hola")), received)
    job.cancel()
  }

  @Test
  fun `effect emitted without a collector waits until one arrives`() = runTest {
    val host = Host()

    with(host) { emitSideEffect(Effect.Message("antes")) }
    advanceUntilIdle()

    assertEquals(Effect.Message("antes"), host.sideEffect.first())
  }

  @Test
  fun `effects keep their emission order`() = runTest {
    val host = Host()

    with(host) {
      emitSideEffect(Effect.Message("uno"))
      emitSideEffect(Effect.Message("dos"))
      emitSideEffect(Effect.Message("tres"))
    }

    assertEquals(
      listOf(Effect.Message("uno"), Effect.Message("dos"), Effect.Message("tres")),
      host.sideEffect.take(3).toList(),
    )
  }

  @Test
  fun `a second collector does not receive an effect already consumed`() = runTest {
    val host = Host()
    with(host) { emitSideEffect(Effect.Message("una vez")) }
    assertEquals(Effect.Message("una vez"), host.sideEffect.first())

    val second = mutableListOf<Effect>()
    val job = launch { host.sideEffect.collect { second += it } }
    advanceUntilIdle()

    assertEquals(emptyList<Effect>(), second)
    job.cancel()
  }
}
