package ru.dimsuz.accord

data class MachineConfig<S, E : Event, C>(
  val id: String,
  val context: C?,
  val initialState: StateNode<S>
)

sealed class StateNode<S> {
  abstract val state: S

  data class Leaf<S>(override val state: S): StateNode<S>()
  data class SubMachine<S>(override val state: S): StateNode<S>()
}
