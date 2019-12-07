package ru.dimsuz.accord

import java.util.*

// TODO
// - move functions out of dsl classes to extension functions
// - test not all states described (have state() block)
// - cond
// - read xstate manual on context (action order etc)

@DslMarker
annotation class StateMachineDsl

@StateMachineDsl
class Machine<S, E : Event, C> {
  var id: String = TODO()
  var context: C = TODO()

  fun states(init: States<S, E, C>.() -> Unit): States<S, E, C> = TODO()
}

@StateMachineDsl
class States<S, E : Event, C> {
  var initial: S = TODO()
  var final: Set<S> = TODO()

  fun state(state: S, init: State<S, E, C>.() -> Unit): State<S, E, C> = TODO()
  fun <SS, CC> machine(state: S, init: SubMachineState<S, C, SS, E, CC>.() -> Unit): SubMachineState<S, C, SS, E, CC> = TODO()
}

@StateMachineDsl
class State<S, E : Event, C> {
  fun on(event: E, init: Transition<S, E, C>.() -> Unit): State<S, E, C> = TODO()
}

@StateMachineDsl
class SubMachineState<S, C, SS, E : Event, CC> {
  fun on(event: E, init: Transition<S, E, C>.() -> Unit): State<S, E, C> = TODO()

  var id: String = TODO()
  var context: CC = TODO()

  fun states(init: States<SS, E, CC>.() -> Unit): States<SS, E, CC> = TODO()
}

@StateMachineDsl
class Transition<S, E : Event, C> {
  fun transitionTo(state: S): Unit = TODO()
  fun guard(predicate: (context: C) -> Boolean): Unit = TODO()
}

fun <S, E : Event, C> machine(init: Machine<S, E, C>.() -> Unit): Machine<S, E, C> = TODO()

abstract class Event {
  open val id: String = UUID.randomUUID().toString()
}
