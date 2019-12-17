package ru.dimsuz.accord

import java.util.UUID

fun <S, E : Event, C> machine(init: Machine<S, E, C>.() -> Unit): MachineConfig<S, E, C> {
  val machine = Machine<S, E, C>()
  machine.init()
  val id = getOrCreateId(machine)
  return MachineConfig(
    id = id,
    context = machine.context,
    initialState = createInitialStateNode(id, machine)
  )
}

private fun <C, E : Event, S> createInitialStateNode(machineId: String, machine: Machine<S, E, C>): StateNode<S> {
  val initial = machine.states.initial ?: error("initial state is missing for '$machineId'")
  if ((machine.states.leafStates.isNotEmpty() && !machine.states.leafStates.containsKey(initial)) ||
    (machine.states.subMachineStates.isNotEmpty() && !machine.states.subMachineStates.containsKey(initial))) {
    error("Initial state $initial not found on '$machineId'")
  }
  return if (machine.states.subMachineStates.isNotEmpty()) {
    StateNode.SubMachine(initial)
  } else {
    StateNode.Leaf(initial)
  }
}

private fun getOrCreateId(machine: Machine<*, *, *>): String {
  if (machine.id?.isBlank() == true) {
    error("machine id must not be blank")
  }
  return machine.id ?: UUID.randomUUID().toString()
}
