package ru.dimsuz.accord

import ru.dimsuz.accord.TransitionsConfig.TargetConfig
import ru.dimsuz.accord.either.Either
import ru.dimsuz.accord.either.Left
import ru.dimsuz.accord.either.Right
import ru.dimsuz.accord.error.ConfigurationError

internal fun <S, E : Event, C> TransitionDsl<S, E, C>.toTargetConfig(): Either<ConfigurationError, TargetConfig<S, E, C>> {
  return Right(
    TargetConfig(
      state = this.state ?: return Left(ConfigurationError("no target state specified")),
      cond = null,
    )
  )
}
