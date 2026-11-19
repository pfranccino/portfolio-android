package dev.pfranccino.portfolio.core.common

/**
 * Resultado de una operación que puede fallar: [Left] lleva el error y [Right] el valor.
 */
sealed interface Either<out L, out R> {
  data class Left<out L>(val value: L) : Either<L, Nothing>
  data class Right<out R>(val value: R) : Either<Nothing, R>
}

/** Envuelve este valor como error. */
fun <L> L.left(): Either<L, Nothing> = Either.Left(this)

/** Envuelve este valor como éxito. */
fun <R> R.right(): Either<Nothing, R> = Either.Right(this)

/** Reduce el resultado a un solo valor, según el lado. */
inline fun <L, R, T> Either<L, R>.fold(
  onLeft: (L) -> T,
  onRight: (R) -> T,
): T = when (this) {
  is Either.Left -> onLeft(value)
  is Either.Right -> onRight(value)
}

/** Transforma el valor de [Either.Right]; deja el error igual. */
inline fun <L, R, T> Either<L, R>.map(transform: (R) -> T): Either<L, T> = when (this) {
  is Either.Left -> this
  is Either.Right -> Either.Right(transform(value))
}

/** Transforma el error de [Either.Left]; deja el valor igual. */
inline fun <L, R, T> Either<L, R>.mapLeft(transform: (L) -> T): Either<T, R> = when (this) {
  is Either.Left -> Either.Left(transform(value))
  is Either.Right -> this
}

/** Encadena otra operación que puede fallar con el valor de [Either.Right]. */
inline fun <L, R, T> Either<L, R>.flatMap(transform: (R) -> Either<L, T>): Either<L, T> =
  when (this) {
    is Either.Left -> this
    is Either.Right -> transform(value)
  }

/** Ejecuta [action] si es [Either.Left] y devuelve el mismo resultado. */
inline fun <L, R> Either<L, R>.onLeft(action: (L) -> Unit): Either<L, R> {
  if (this is Either.Left) action(value)
  return this
}

/** Ejecuta [action] si es [Either.Right] y devuelve el mismo resultado. */
inline fun <L, R> Either<L, R>.onRight(action: (R) -> Unit): Either<L, R> {
  if (this is Either.Right) action(value)
  return this
}

/** El valor de [Either.Right], o `null` si es [Either.Left]. */
fun <L, R> Either<L, R>.getOrNull(): R? = (this as? Either.Right)?.value
