package ru.dimsuz.accord.sample

import ru.dimsuz.accord.Event
import ru.dimsuz.accord.machine
import ru.dimsuz.accord.sample.flow.core.MyAppFlow
import ru.dimsuz.accord.sample.flow.login.loginFlowState
import ru.dimsuz.accord.sample.flow.otp.otpFlowState

fun main() {
  machine<MyAppFlow, Event, Map<String, Int>> {
    states {
      initial = MyAppFlow.FlowLogin
      loginFlowState()
      otpFlowState()
    }
  }
}
