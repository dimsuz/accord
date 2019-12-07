package ru.dimsuz.accord.sample.flow.otp

import ru.dimsuz.accord.Event
import ru.dimsuz.accord.States
import ru.dimsuz.accord.sample.flow.core.MyAppEvent
import ru.dimsuz.accord.sample.flow.core.MyAppFlow

enum class OtpFlowState {
  ScreenOtpIntro,
  ScreenOtpInput,

  FinishedSuccessfully,
  Dismissed
}

sealed class OtpEvent : Event() {
  object OtpIntroContinue : OtpEvent()
  object OtpInputSuccess : OtpEvent()
}

fun States<MyAppFlow, Event, Map<String, Int>>.otpFlowState() {
  machine<OtpFlowState, Unit>(MyAppFlow.FlowOtp) {
    on(MyAppEvent.Done) {
      // TODO check if success or no
      transitionTo(MyAppFlow.FlowPinCreate)
    }

    states {
      initial = OtpFlowState.ScreenOtpIntro
      final = setOf(OtpFlowState.Dismissed, OtpFlowState.FinishedSuccessfully)

      state(OtpFlowState.ScreenOtpIntro) {
        on(MyAppEvent.Back) { transitionTo(OtpFlowState.Dismissed) }
        on(OtpEvent.OtpIntroContinue) { transitionTo(OtpFlowState.ScreenOtpInput) }
      }
      state(OtpFlowState.ScreenOtpInput) {
        on(MyAppEvent.Back) { transitionTo(OtpFlowState.ScreenOtpIntro) }
        on(OtpEvent.OtpInputSuccess) { transitionTo(OtpFlowState.FinishedSuccessfully) }
      }
    }
  }
}
