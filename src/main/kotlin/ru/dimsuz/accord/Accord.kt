package ru.dimsuz.accord

import java.util.*

// TODO
// - test not all states described (have state() block)
// - cond
// - read xstate manual on context (action order etc)
// - support for send,raise etc actions?

@DslMarker
annotation class StateMachineDsl

@StateMachineDsl
class Machine<S, E : Event, C> {
  var id: String? = null
  var context: C? = null

  internal val states: States<S, E, C> = States()

  fun states(init: States<S, E, C>.() -> Unit) = states.init()
}

@StateMachineDsl
class States<S, E : Event, C> {
  var initial: S? = null
  var final: Set<S> = emptySet()

  internal val atomicStates = mutableMapOf<S, State<S, E, C>>()
  internal val compoundStates = mutableMapOf<S, CompoundState<S, C, *, E, *>>()

  fun state(state: S, init: State<S, E, C>.() -> Unit) {
    atomicStates[state] = State()
  }

  // TODO call `compoundState`? Otherwise it's easy to just call top-level `machine` from `states { }` block
  //  and get confused
  fun <SS, CC> machine(state: S, init: CompoundState<S, C, SS, E, CC>.() -> Unit) {
    compoundStates[state] = CompoundState<S, C, SS, E, CC>()
  }
}

@StateMachineDsl
class Transitions<S, E : Event, C> {
  fun on(event: E, init: Transition<S, E, C>.() -> Unit): State<S, E, C> = TODO()
}

@StateMachineDsl
class State<S, E : Event, C> {
  fun transitions(init: Transitions<S, E, C>.() -> Unit): Unit = TODO()
  fun actions(init: StateActions<C, E, S>.() -> Unit): Unit = TODO()
}

@StateMachineDsl
class CompoundState<S, C, SS, E : Event, CC> {
  fun transitions(init: Transitions<S, E, C>.() -> Unit): Unit = TODO()

  var id: String? = null
  var context: CC? = null

  fun states(init: States<SS, E, CC>.() -> Unit): States<SS, E, CC> = TODO()
}

@StateMachineDsl
class Transition<S, E : Event, C> {
  fun transitionTo(state: S): Unit = TODO()
  fun guard(predicate: (context: C) -> Boolean): Unit = TODO()
  fun action(action: MachineAction<C, E, S>): Unit = TODO()
}

@StateMachineDsl
class StateActions<S, E : Event, C> {
  fun onEntry(action: MachineAction<C, E, S>): Unit = TODO()
  fun onExit(action: MachineAction<C, E, S>): Unit = TODO()
}

typealias MachineAction<C, E, S> = (C, E, S) -> Unit

abstract class Event {
  open val id: String = UUID.randomUUID().toString()
}
