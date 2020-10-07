package ru.dimsuz.accord

import kotlin.reflect.KClass

data class MachineConfig<S, E : Event, C>(
  val rootState: StateConfig.Compound<Any, E, C, S>
) {
  val id get() = rootState.id
  val context get() = rootState.context
  val initialState get() = rootState.initialState
}

sealed class StateConfig<S> {
  abstract val state: S

  data class Atomic<S, E : Event, C>(
    override val state: S,
    val transitions: TransitionsConfig<S, E, C>
  ): StateConfig<S>()

  data class Compound<S, E : Event, C, SS>(
    override val state: S,
    val id: String,
    val context: C?,
    val initialState: StateConfig<SS>,
    val states: List<StateConfig<SS>>,
    val transitions: TransitionsConfig<SS, E, C>
  ): StateConfig<S>()
}

data class TransitionsConfig<S, E : Event, C>(
  val eventTargets: Map<KClass<E>, List<TargetConfig<S, E, C>>>
) {
  data class TargetConfig<S, E : Event, C>(
    val state: S,
    val cond: (context: C, event: E) -> Boolean
  )
}
