package dev.pfranccino.portfolio.core.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EitherTest {
  private val right: Either<String, Int> = 2.right()
  private val left: Either<String, Int> = "error".left()

  @Test
  fun `fold elige el lado correcto`() {
    assertEquals("r2", right.fold({ "l$it" }, { "r$it" }))
    assertEquals("lerror", left.fold({ "l$it" }, { "r$it" }))
  }

  @Test
  fun `map transforma solo el Right`() {
    assertEquals(Either.Right(4), right.map { it * 2 })
    assertEquals(Either.Left("error"), left.map { it * 2 })
  }

  @Test
  fun `mapLeft transforma solo el Left`() {
    assertEquals(Either.Left(5), left.mapLeft { it.length })
    assertEquals(Either.Right(2), right.mapLeft { it.length })
  }

  @Test
  fun `flatMap encadena y corta en el primer Left`() {
    assertEquals(Either.Right("2!"), right.flatMap { "$it!".right() })
    assertEquals(Either.Left("otro"), right.flatMap { "otro".left() })
    assertEquals(Either.Left("error"), left.flatMap { "$it!".right() })
  }

  @Test
  fun `onLeft y onRight ejecutan solo su lado`() {
    val seen = mutableListOf<String>()
    right.onLeft { seen += "l" }.onRight { seen += "r" }
    left.onLeft { seen += "L" }.onRight { seen += "R" }
    assertEquals(listOf("r", "L"), seen)
  }

  @Test
  fun `getOrNull devuelve el valor o null`() {
    assertEquals(2, right.getOrNull())
    assertNull(left.getOrNull())
  }
}
