package ru.dimsuz.accord.sample

import ru.dimsuz.accord.Event
import ru.dimsuz.accord.machine
import ru.dimsuz.accord.sample.flow.core.MyAppFlow
import ru.dimsuz.accord.sample.flow.login.loginFlowState
import ru.dimsuz.accord.sample.flow.otp.otpFlowState

private enum class Test3States {
  S31,
  S32,
  S33
}

fun main() {
  machine<Test3States, Event, Unit> {
    id = "test_3_states"
    states {
      initial = Test3States.S31
      state(Test3States.S32) {}
      state(Test3States.S33) {}
    }
  }
  machine<MyAppFlow, Event, Map<String, Int>> {
    id = "root_app_flow"
    states {
      initial = MyAppFlow.FlowLogin
      loginFlowState()
      otpFlowState()
    }
  }
}
