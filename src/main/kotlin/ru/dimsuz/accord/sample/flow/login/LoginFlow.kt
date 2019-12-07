package ru.dimsuz.accord.sample.flow.login

import ru.dimsuz.accord.Event
import ru.dimsuz.accord.States
import ru.dimsuz.accord.sample.flow.core.MyAppEvent
import ru.dimsuz.accord.sample.flow.core.MyAppFlow

enum class LoginFlowState {
  ScreenOnboarding,
  ScreenLogin,

  FinishedOtpRequired,
  FinishedOtpNotRequired,
  Dismissed
}

sealed class LoginEvent : Event() {
  object OnboardingFinished : LoginEvent()
  object LoginSuccessOtpRequired : LoginEvent()
  object LoginSuccessOtpNotRequired : LoginEvent()
}

fun States<MyAppFlow, Event, Map<String, Int>>.loginFlowState() {
  machine<LoginFlowState, Boolean>(MyAppFlow.FlowLogin) {
    on(LoginEvent.LoginSuccessOtpRequired) { transitionTo(MyAppFlow.FlowOtp) }
    on(LoginEvent.LoginSuccessOtpNotRequired) {
      transitionTo(MyAppFlow.FlowPinCreate)
      guard { context -> context.isEmpty() }
    }
    on(MyAppEvent.Done) {
      // TODO check if otp required or not
      transitionTo(MyAppFlow.FlowPinCreate)
    }
    states {
      initial = LoginFlowState.ScreenOnboarding
      final = setOf(LoginFlowState.FinishedOtpRequired, LoginFlowState.FinishedOtpNotRequired)

      state(LoginFlowState.ScreenOnboarding) {
        on(LoginEvent.OnboardingFinished) { transitionTo(LoginFlowState.ScreenLogin) }
        on(MyAppEvent.Back) { transitionTo(LoginFlowState.Dismissed) }
      }
      state(LoginFlowState.ScreenLogin) {
        on(MyAppEvent.Back) { transitionTo(LoginFlowState.ScreenOnboarding) }
        on(LoginEvent.LoginSuccessOtpRequired) { transitionTo(LoginFlowState.FinishedOtpRequired) }
      }
    }
  }
}
