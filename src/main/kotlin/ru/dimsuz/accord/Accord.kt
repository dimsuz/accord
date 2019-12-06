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
  var initial: S = TODO()
  var final: S = TODO()
  var context: C = TODO()

  fun states(init: States<S, E, C>.() -> Unit): States<S, E, C> = TODO()
}

@StateMachineDsl
class States<S, E : Event, C> {
  fun state(state: S, init: State<S, E, C>.() -> Unit): State<S, E, C> = TODO()
  fun finalState(state: S): Unit = TODO()
}

@StateMachineDsl
class State<S, E : Event, C> {
  fun on(event: E, init: Transition<S, E, C>.() -> Unit): State<S, E, C> = TODO()
  fun <SS, CC> states(init: States<SS, E, CC>.() -> Unit): States<SS, E, CC> = TODO()
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
