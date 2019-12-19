package ru.dimsuz.accord

data class MachineConfig<S, E : Event, C>(
  val id: String,
  val context: C?,
  val initialState: StateConfig<S>
)

sealed class StateConfig<S> {
  abstract val state: S

  data class Atomic<S>(override val state: S): StateConfig<S>()
  data class Compound<S>(override val state: S): StateConfig<S>()
}
