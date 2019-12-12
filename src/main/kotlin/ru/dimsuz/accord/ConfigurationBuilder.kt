package ru.dimsuz.accord

import java.util.UUID

fun <S, E : Event, C> machine(init: Machine<S, E, C>.() -> Unit): MachineConfig<S, E, C> {
  val machine = Machine<S, E, C>()
  machine.init()
  return MachineConfig(
    id = getOrCreateId(machine),
    context = machine.context
  )
}

private fun getOrCreateId(machine: Machine<*, *, *>): String {
  if (machine.id?.isBlank() == true) {
    error("machine id must not be blank")
  }
  return machine.id ?: UUID.randomUUID().toString()
}
