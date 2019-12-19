package ru.dimsuz.accord

import java.util.UUID

fun <S, E : Event, C> machine(init: MachineDsl<S, E, C>.() -> Unit): MachineConfig<S, E, C> {
  val machine = MachineDsl<S, E, C>()
  machine.init()
  val rootStateDsl = StatesDsl<Unit, E, Unit>().apply {
    initial = Unit
    compoundStates[Unit] = CompoundStateDsl<Unit, Unit, S, E, C>(machine.states).apply {
      this.id = machine.id
      this.context = machine.context
    }
  }
  return MachineConfig(
    rootState = createSubStateConfigs("root-id", rootStateDsl).single() as StateConfig.Compound<Any, S, C>
  )
}

//private fun <C, E : Event, S> createInitialStateConfig(
//  machineId: String,
//  machine: MachineDsl<S, E, C>): StateConfig<S> {
//  val initial = machine.states.initial ?: error("initial state is missing for '$machineId'")
//  if ((machine.states.atomicStates.isNotEmpty() && !machine.states.atomicStates.containsKey(initial)) ||
//    (machine.states.compoundStates.isNotEmpty() && !machine.states.compoundStates.containsKey(initial))) {
//    error("Initial state $initial not found on '$machineId'")
//  }
//  return if (machine.states.compoundStates.isNotEmpty()) {
//    StateConfig.Compound(initial, )
//  } else {
//    StateConfig.Atomic(initial)
//  }
//}

fun <S, E : Event, C> createSubStateConfigs(
  parentStateId: String,
  states: StatesDsl<S, E, C>
): List<StateConfig<S>> {
//  if (states.atomicStates.isNotEmpty() && states.compoundStates.isNotEmpty()) {
//    error("fjf")
//  }
  return if (states.atomicStates.isNotEmpty()) {
    states.atomicStates.map { (state, stateDsl) -> createAtomicStateConfig(state, stateDsl) }
  } else {
    states.compoundStates.map { (state, stateDsl) -> createCompoundStateConfig(state, stateDsl) }
  }
}

fun <S, E : Event, C> createAtomicStateConfig(state: S, stateDsl: StateDsl<S, E, C>): StateConfig<S> {
  return StateConfig.Atomic(state)
}

fun <S, C, E : Event> createCompoundStateConfig(state: S, stateDsl: CompoundStateDsl<S, C, *, E, *>): StateConfig<S> {
  val id = getOrCreateId(stateDsl)
  val subStates = createSubStateConfigs(id, stateDsl.states) as List<StateConfig<Any>>
  val initialState = stateDsl.states.initial ?: error("initial state is not specified for '$id'")
  println("searching for ${stateDsl.states.initial} in ${subStates.map { it.state }}")
  return StateConfig.Compound(
    id = id,
    state = state,
    context = stateDsl.context,
    initialState = subStates.find { it.state == initialState } ?: error("Initial state $initialState not found on '$id'"),
    states = subStates
  )
}


private fun getOrCreateId(machine: CompoundStateDsl<*, *, *, *, *>): String {
  if (machine.id?.isBlank() == true) {
    error("id must not be blank")
  }
  return machine.id ?: UUID.randomUUID().toString()
}
