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
class MachineDsl<S, E : Event, C> {
  var id: String? = null
  var context: C? = null

  internal val states: StatesDsl<S, E, C> = StatesDsl()

  fun states(init: StatesDsl<S, E, C>.() -> Unit) = states.init()
}

@StateMachineDsl
class StatesDsl<S, E : Event, C> {
  var initial: S? = null
  var final: Set<S> = emptySet()

  internal val atomicStates = mutableMapOf<S, StateDsl<S, E, C>>()
  internal val compoundStates = mutableMapOf<S, CompoundStateDsl<S, E, C, *, *>>()

  fun state(state: S, init: StateDsl<S, E, C>.() -> Unit) {
    atomicStates[state] = StateDsl<S, E, C>().apply { init() }
  }

  // TODO call `compoundState`? Otherwise it's easy to just call top-level `machine` from `states { }` block
  //  and get confused
  fun <SubState : Any, SubContext> machine(
    state: S,
    init: CompoundStateDsl<S, E, C, SubState, SubContext>.() -> Unit
  ) {
    compoundStates[state] = CompoundStateDsl<S, E, C, SubState, SubContext>().apply {
      init()
    }
  }
}

@StateMachineDsl
class TransitionsDsl<S, E : Event, C> {
  fun on(event: E, init: TransitionDsl<S, E, C>.() -> Unit): StateDsl<S, E, C> = TODO()
}

@StateMachineDsl
class StateDsl<S, E : Event, C> {
  fun transitions(init: TransitionsDsl<S, E, C>.() -> Unit): Unit = TODO()
  fun actions(init: StateActionsDsl<C, E, S>.() -> Unit): Unit = TODO()
}

@StateMachineDsl
class CompoundStateDsl<S, E : Event, C, SS, CS>(
  val states: StatesDsl<SS, E, CS> = StatesDsl()
) {
  fun transitions(init: TransitionsDsl<S, E, C>.() -> Unit): Unit = TODO()

  var id: String? = null
  var context: CS? = null

  fun states(init: StatesDsl<SS, E, CS>.() -> Unit) {
    states.init()
  }
}

@StateMachineDsl
class TransitionDsl<S, E : Event, C> {
  fun transitionTo(state: S): Unit = TODO()
  fun cond(predicate: (context: C) -> Boolean): Unit = TODO()
  fun action(action: MachineAction<C, E, S>): Unit = TODO()
}

@StateMachineDsl
class StateActionsDsl<S, E : Event, C> {
  fun onEntry(action: MachineAction<C, E, S>): Unit = TODO()
  fun onExit(action: MachineAction<C, E, S>): Unit = TODO()
}

typealias MachineAction<C, E, S> = (C, E, S) -> Unit

abstract class Event {
  open val id: String = UUID.randomUUID().toString()
}
