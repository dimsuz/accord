package ru.dimsuz.accord

import ru.dimsuz.accord.either.mapValuesE
import ru.dimsuz.accord.either.sequenceA
import java.util.UUID

fun <S, E : Event, C> machine(init: MachineDsl<S, E, C>.() -> Unit): MachineConfig<S, E, C> {
  val machine = MachineDsl<S, E, C>()
  machine.init()
  val rootStateDsl = StatesDsl<Unit, E, Unit>().apply {
    initial = Unit
    compoundStates[Unit] = CompoundStateDsl<Unit, E, Unit, S, C>(machine.states).apply {
      this.id = machine.id
      this.context = machine.context
    }
  }
  return MachineConfig(
    rootState = createSubStateConfigs(rootStateDsl).single()  as StateConfig.Compound<Any, E, C, S>
  )
}

private fun <S, E : Event, C> createSubStateConfigs(
  states: StatesDsl<S, E, C>
): List<StateConfig<S>> {
  if (states.atomicStates.isNotEmpty() && states.compoundStates.isNotEmpty()) {
    val firstAtomic = states.atomicStates.keys.first()
    val firstCompound = states.compoundStates.keys.first()
    error("mixing atomic ['$firstAtomic'] and compound ['$firstCompound'] states is not allowed")
  }
  return if (states.atomicStates.isNotEmpty()) {
    states.atomicStates.map { (state, stateDsl) -> createAtomicStateConfig(state, stateDsl) }
  } else {
    states.compoundStates.map { (state, stateDsl) -> createCompoundStateConfig(state, stateDsl) }
  }
}

private fun <S, E : Event, C> createAtomicStateConfig(state: S, stateDsl: StateDsl<S, E, C>): StateConfig<S> {
  val eventTargets = stateDsl.transitionsDsl?.transitions?.mapValuesE { transitionDslList ->
    transitionDslList.map { it.toTargetConfig() }.sequenceA()
  }
  return StateConfig.Atomic(state, TransitionsConfig<S, E, C>(emptyMap()))
}

private fun <S, E : Event, C, SS, CS> createCompoundStateConfig(state: S, stateDsl: CompoundStateDsl<S, E, C, SS, CS>): StateConfig<S> {
  val id = getOrCreateId(stateDsl)
  val subStates = createSubStateConfigs(stateDsl.states)
  val initialState = stateDsl.states.initial ?: error("initial state is not specified for '$id'")
  return StateConfig.Compound<S, E, CS, SS>(
    id = id,
    state = state,
    context = stateDsl.context,
    initialState = subStates.find { it.state == initialState } ?: error("Initial state $initialState not found on '$id'"),
    states = subStates,
    transitions = TransitionsConfig(emptyMap())
  )
}

private fun getOrCreateId(machine: CompoundStateDsl<*, *, *, *, *>): String {
  if (machine.id?.isBlank() == true) {
    error("id must not be blank")
  }
  return machine.id ?: UUID.randomUUID().toString()
}
