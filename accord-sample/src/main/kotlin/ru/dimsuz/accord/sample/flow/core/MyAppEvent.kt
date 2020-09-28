package ru.dimsuz.accord.sample.flow.core

import ru.dimsuz.accord.Event

sealed class MyAppEvent : Event() {
  // TODO find out how to handle globally
  object RefreshTokenExpired : MyAppEvent()
  object Back : MyAppEvent()
  // TODO must be internal
  object Done : MyAppEvent()
}

