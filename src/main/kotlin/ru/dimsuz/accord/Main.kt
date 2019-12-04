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
  fun finalState(state: S): Unit = TODO()
}

@StateMachineDsl
class State<S, E, C> {
  fun on(event: E, init: Transition<S, E, C>.() -> Unit): State<S, E, C> = TODO()
  fun <SS, CC> states(init: States<SS, E, CC>.() -> Unit): States<SS, E, CC> = TODO()
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
  // TODO find out how to handle globally
  RefreshTokenExpired,
  Back,

  // Login
  OnboardingFinished,
  LoginSuccessOtpRequired,
  LoginSuccessOtpNotRequired,

  // Otp
  OtpIntroContinue,
  OtpInputSuccess,

  // TODO must be internal
  Done
}

enum class LoginFlowState {
  ScreenOnboarding,
  ScreenLogin,

  FinishedOtpRequired,
  FinishedOtpNotRequired,
  Dismissed
}

enum class OtpFlowState {
  ScreenOtpIntro,
  ScreenOtpInput,

  FinishedSuccessfully,
  Dismissed
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
        on(FlowEvent.Done) {
          // TODO check if otp required or not
          transitionTo(FlowState.FlowPinCreate)
        }
        states<LoginFlowState, Boolean> {
          state(LoginFlowState.ScreenOnboarding) {
            on(FlowEvent.OnboardingFinished) { transitionTo(LoginFlowState.ScreenLogin) }
            on(FlowEvent.Back) { transitionTo(LoginFlowState.Dismissed) }
          }
          state(LoginFlowState.ScreenLogin) {
            on(FlowEvent.Back) { transitionTo(LoginFlowState.ScreenOnboarding) }
            on(FlowEvent.LoginSuccessOtpRequired) { transitionTo(LoginFlowState.FinishedOtpRequired) }
          }
          finalState(LoginFlowState.FinishedOtpRequired)
          finalState(LoginFlowState.FinishedOtpNotRequired)
        }
      }
      state(FlowState.FlowOtp) {
        on(FlowEvent.Done) {
          // TODO check if success or no
          transitionTo(FlowState.FlowPinCreate)
        }

        states<OtpFlowState, Unit> {
          state(OtpFlowState.ScreenOtpIntro) {
            on(FlowEvent.Back) { transitionTo(OtpFlowState.Dismissed) }
            on(FlowEvent.OtpIntroContinue) { transitionTo(OtpFlowState.ScreenOtpInput) }
          }
          state(OtpFlowState.ScreenOtpInput) {
            on(FlowEvent.Back) { transitionTo(OtpFlowState.ScreenOtpIntro) }
            on(FlowEvent.OtpInputSuccess) { transitionTo(OtpFlowState.FinishedSuccessfully) }
          }
          finalState(OtpFlowState.Dismissed)
          finalState(OtpFlowState.FinishedSuccessfully)
        }
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
