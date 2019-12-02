package ru.dimsuz.accord

@DslMarker
annotation class StateMachineDsl

@StateMachineDsl
class Machine<S, E, C> {
  var id: String = TODO()
  var initial: S = TODO()
  var final: S = TODO()
  var context: C = TODO()

  fun states(init: States<S, E, C>.() -> Unit): States<S, E, C> = TODO()
}

@StateMachineDsl
class States<S, E, C> {
  fun state(state: S, init: State<S, E, C>.() -> Unit): State<S, E, C> = TODO()
}

@StateMachineDsl
class State<S, E, C> {
  fun on(event: E, init: Transition<S, E, C>.() -> Unit): State<S, E, C> = TODO()
}

@StateMachineDsl
class Transition<S, E, C> {
  fun transitionTo(state: S): Unit = TODO()
  fun guard(predicate: (context: C) -> Boolean): Unit = TODO()
}

fun <S, E, C> machine(init: Machine<S, E, C>.() -> Unit): Machine<S, E, C> = TODO()

enum class FlowState {
  FlowLogin,
  FlowOtp,
  FlowPinCreate
}

enum class FlowEvent {
  LoginSuccessOtpRequired,
  LoginSuccessOtpNotRequired
}

fun main(args: Array<String>) {
  machine<FlowState, FlowEvent, Map<String, Int>> {
    id = "flow_app"
    initial = FlowState.FlowLogin
    context = emptyMap()

    states {
      state(FlowState.FlowLogin) {
        on(FlowEvent.LoginSuccessOtpRequired) { transitionTo(FlowState.FlowOtp) }
        on(FlowEvent.LoginSuccessOtpNotRequired) {
          transitionTo(FlowState.FlowPinCreate)
          guard { context -> context.isEmpty() }
        }
      }
      state(FlowState.FlowOtp) {

      }
      state(FlowState.FlowPinCreate) {

      }
    }
  }
}

// TODO
// - test not all states described (have state() block)
// - cond
// - read xstate manual on context (action order etc)
