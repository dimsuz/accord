package ru.dimsuz.accord

data class MachineConfig<S, E : Event, C>(
  val id: String,
  val context: C
)
