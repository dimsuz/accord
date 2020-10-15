package ru.dimsuz.accord.either

internal sealed class Either<out L, out R>

internal class Left<out T>(val value: T) : Either<T, Nothing>()
internal class Right<out T>(val value: T) : Either<Nothing, T>()

internal fun <L, R, T> Either<L, R>.fold(left: (L) -> T, right: (R) -> T): T =
  when (this) {
    is Left -> left(value)
    is Right -> right(value)
  }

internal fun <L, R, T> Either<L, R>.flatMap(f: (R) -> Either<L, T>): Either<L, T> =
  fold({ this as Left }, f)

internal fun <L, R, T> Either<L, R>.map(f: (R) -> T): Either<L, T> =
  flatMap { Right(f(it)) }

internal fun <L, R, T> Either<L, R>.mapLeft(f: (L) -> T): Either<T, R> =
  fold({ Left(f(it)) }, { this as Right })

internal fun <L, R> List<Either<L, R>>.join(): Either<L, List<R>> {
  if (isEmpty()) return Right(emptyList())
  val initial = first().map { mutableListOf(it) }
  return if (size == 1) initial else {
    drop(1).fold(initial) { acc, either ->
      acc.flatMap { list -> either.map { list.add(it); list } }
    }
  }
}

internal fun <L, R1, R2, V> lift2(
  e1: Either<L, R1>,
  e2: Either<L, R2>,
  f: (R1, R2) -> V
): Either<L, V> {
  return when {
    e1 is Right && e2 is Right -> Right(f(e1.value, e2.value))
    e1 is Left -> e1
    e2 is Left -> e2
    else -> error("impossiburu!")
  }
}

internal fun <L, R1, R2, R3, V> lift3(
  e1: Either<L, R1>,
  e2: Either<L, R2>,
  e3: Either<L, R3>,
  f: (R1, R2, R3) -> V
): Either<L, V> {
  return when {
    e1 is Right && e2 is Right && e3 is Right -> Right(f(e1.value, e2.value, e3.value))
    e1 is Left -> e1
    e2 is Left -> e2
    e3 is Left -> e3
    else -> error("impossiburu!")
  }
}

internal fun <L, R1, R2, R3, R4, V> lift4(
  e1: Either<L, R1>,
  e2: Either<L, R2>,
  e3: Either<L, R3>,
  e4: Either<L, R4>,
  f: (R1, R2, R3, R4) -> V
): Either<L, V> {
  return when {
    e1 is Right && e2 is Right && e3 is Right && e4 is Right -> Right(f(e1.value, e2.value, e3.value, e4.value))
    e1 is Left -> e1
    e2 is Left -> e2
    e3 is Left -> e3
    e4 is Left -> e4
    else -> error("impossiburu!")
  }
}

internal fun <L, R> R?.toRightOr(leftValue: L): Either<L, R> {
  return if (this == null) Left(leftValue) else Right(this)
}

internal inline fun <K, L, R1, R2> Map<K, R1>.mapValuesE(transform: (R1) -> Either<L, R2>): Either<L, Map<K, R2>> {
  return Right(this.mapValues { (_, value) ->
    when (val result = transform(value)) {
      is Left -> return result
      is Right -> result.value
    }
  })
}

internal fun <L, R> List<Either<L, R>>.sequenceA(): Either<L, List<R>> {
  return Right(this.map { elem ->
    when (elem) {
      is Left -> return elem
      is Right -> elem.value
    }
  })
}
