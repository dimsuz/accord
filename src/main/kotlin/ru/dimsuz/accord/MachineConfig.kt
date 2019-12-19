package ru.dimsuz.accord

data class MachineConfig<S, E : Event, C>(
  val rootState: StateConfig.Compound<Any, S, C>
) {
  val id get() = rootState.id
  val context get() = rootState.context
  val initialState get() = rootState.initialState
}

sealed class StateConfig<S> {
  abstract val state: S

  data class Atomic<S>(override val state: S): StateConfig<S>()
  data class Compound<S, CS, C>(
    override val state: S,
    val id: String,
    val context: C?,
    val initialState: StateConfig<CS>,
    val states: List<StateConfig<CS>>
  ): StateConfig<S>()
}
