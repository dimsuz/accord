package ru.dimsuz.accord

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.IllegalStateException

class DslConfigurationTest {

  // region BasicConfig

  @Test
  fun `given missing machine id should generate a default one`() {
    val config = machine<Test1States, Event, Unit> {
      addFakeStates()
    }
    assertThat(config.id)
      .isNotEmpty()
  }

  @Test
  fun `given non-empty machine id should use it`() {
    val config = machine<Test1States, Event, Unit> {
      id = "my-id"
      addFakeStates()
    }
    assertThat(config.id)
      .isEqualTo("my-id")
  }

  @Test
  fun `given empty machine id should throw config exception`() {
    try {
      machine<Test1States, Event, Unit> {
        id = ""
        addFakeStates()
      }
      error("expected machine configuration to throw")
    } catch (e: IllegalStateException) {
      assertThat(e)
        .hasMessageThat()
        .containsMatch("id.*blank")
    }
  }

  @Test
  fun `given blank machine id should throw config exception`() {
    try {
      machine<Test1States, Event, Unit> {
        id = "    "
        addFakeStates()
      }
      error("expected machine configuration to throw")
    } catch (e: IllegalStateException) {
      assertThat(e)
        .hasMessageThat()
        .containsMatch("id.*blank")
    }
  }

  @Test
  fun `given a context uses it in config`() {
    val config = machine<Test1States, Event, List<Int>> {
      context = arrayListOf(1, 3, 3)
      addFakeStates()
    }

    assertThat(config.context)
      .isEqualTo(arrayListOf(1, 3, 3))
  }

  // endregion

  // region States
  @Test
  fun `given a machine with no initial state should report an error`() {
    try {
      machine<Test3States, Event, Unit> {
        states { }
      }
      error("expected machine configuration to throw")
    } catch (e: IllegalStateException) {
      assertThat(e)
        .hasMessageThat()
        .containsMatch("initial state")
    }
  }

  @Test
  fun `given a machine with atomic initial state should save it in config`() {
    val config = machine<Test3States, Event, Unit> {
      states {
        initial = Test3States.S33
        state(Test3States.S31) { }
        state(Test3States.S32) { }
        state(Test3States.S33) { }
      }
    }

    assertThat(config.initialState)
      .isInstanceOf(StateConfig.Atomic::class.java)
    assertThat(config.initialState.state)
      .isEqualTo(Test3States.S33)
  }

  @Test
  fun `given a machine with compound initial state should save it in config`() {
    val config = machine<Test3States, Event, Unit> {
      states {
        initial = Test3States.S32
        machine<Test1States, Unit>(Test3States.S31) {
          addFakeCompoundStates()
        }
        machine<Test1States, Unit>(Test3States.S32) {
          addFakeCompoundStates()
        }
        machine<Test1States, Unit>(Test3States.S33) {
          addFakeCompoundStates()
        }
      }
    }

    assertThat(config.initialState)
      .isInstanceOf(StateConfig.Compound::class.java)
    assertThat(config.initialState.state)
      .isEqualTo(Test3States.S32)
  }

  @Test
  fun `given a machine with missing atomic initial state should report an error`() {
    try {
      machine<Test3States, Event, Unit> {
        id = "test_3_states"
        states {
          initial = Test3States.S31
          state(Test3States.S32) {}
          state(Test3States.S33) {}
        }
      }
      error("expected machine configuration to throw")
    } catch (e: IllegalStateException) {
      assertThat(e)
        .hasMessageThat()
        .containsMatch("Initial state.* not found on.*")
    }
  }

  @Test
  fun `given a machine with missing compound initial state should report an error`() {
    try {
      machine<Test3States, Event, Unit> {
        id = "machine_3_state"
        states {
          initial = Test3States.S31
          machine<Test1States, Unit>(Test3States.S32) {
            addFakeCompoundStates()
          }
          machine<Test1States, Unit>(Test3States.S33) {
            addFakeCompoundStates()
          }
        }
      }
      error("expected machine configuration to throw")
    } catch (e: IllegalStateException) {
      assertThat(e)
        .hasMessageThat()
        .containsMatch("Initial state.* not found on.*")
    }
  }

  @Test
  fun `given a machine with mixed atomic and compound states should report an error`() {
    try {
      machine<Test3States, Event, Unit> {
        states {
          initial = Test3States.S31
          state(Test3States.S31) { }
          state(Test3States.S32) { }
          machine<Test1States, Unit>(Test3States.S33) {
            addFakeCompoundStates()
          }
        }
      }
      error("expected machine configuration to throw")
    } catch (e: IllegalStateException) {
      val atomicStateName = Test3States.S31.toString()
      val compoundStateName = Test3States.S33.toString()
      assertThat(e)
        .hasMessageThat()
        .contains("mixing atomic ['$atomicStateName'] and compound ['$compoundStateName'] states is not allowed")
    }
  }
  // endregion

  // region Transitions

  @Test
  fun `given atomic state with no transitions is accepted as valid config`() {
    var error: Throwable? = null
    try {
      machine<Test3States, TestEvent, Unit> {
        states {
          initial = Test3States.S31
          state(Test3States.S31) {
          }
        }
      }
    } catch (e: Throwable) {
      error = e
    }

    assertThat(error).isNull()
  }

  @Test
  fun `given compound state with no transitions is accepted as valid config`() {
    var error: Throwable? = null
    try {
      machine<Test3States, TestEvent, Unit> {
        states {
          initial = Test3States.S31
          machine<Test1States, Unit>(Test3States.S31) {
            states {
              initial = Test1States.S11
              state(Test1States.S11) {}
            }
          }
        }
      }
    } catch (e: Throwable) {
      error = e
    }

    assertThat(error).isNull()
  }

  @Test
  fun `given atomic state transitions should save them`() {
    val config = machine<Test3States, TestEvent, Unit> {
      states {
        initial = Test3States.S31
        state(Test3States.S31) {
          transitions {
            on<TestEvent.E2> {
              transitionTo(Test3States.S32)
            }
          }
        }
      }
    }

    val stateConfig = config.rootState.states.firstOrNull() as StateConfig.Atomic<*, *, *>?
    assertThat(stateConfig?.transitions?.eventTargets)
      .containsKey(TestEvent.E2::class)
  }

  @Suppress("SimplifyBooleanWithConstants")
  @Test
  fun `given atomic state multiple transitions on single event should save them`() {
    val config = machine<Test3States, TestEvent, Boolean> {
      states {
        initial = Test3States.S31
        state(Test3States.S31) {
          transitions {
            on<TestEvent.E1> {
              transitionTo(Test3States.S32)
              cond { context, _ -> context == true }
            }
            on<TestEvent.E1> {
              transitionTo(Test3States.S33)
              cond { context, _ -> context == false }
            }
          }
        }
        state(Test3States.S33) {}
        state(Test3States.S32) {}
      }
    }

    val stateConfig = config.rootState.states.firstOrNull() as StateConfig.Atomic<*, *, *>?
    assertThat(stateConfig?.transitions?.eventTargets)
      .containsKey(TestEvent.E1::class)
    assertThat(stateConfig?.transitions?.eventTargets?.get(TestEvent.E1::class))
      .hasSize(2)
  }
  // endregion

  private fun <E : Event, C> MachineDsl<Test1States, E, C>.addFakeStates() {
    states {
      initial = Test1States.S11
      state(Test1States.S11) {}
    }
  }

  private fun <S, E : Event, C, CS> CompoundStateDsl<S, E, C, Test1States, CS>.addFakeCompoundStates() {
    states {
      initial = Test1States.S11
      state(Test1States.S11) {}
    }
  }


  private enum class Test3States {
    S31,
    S32,
    S33
  }

  private enum class Test1States {
    S11
  }

  private sealed class TestEvent : Event() {
    data class E1(val value: Int) : TestEvent()
    data class E2(val value: String) : TestEvent()
  }
}


