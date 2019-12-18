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
        initial = Test3States.S3
        state(Test3States.S1) { }
        state(Test3States.S2) { }
        state(Test3States.S3) { }
      }
    }

    assertThat(config.initialState)
      .isInstanceOf(StateNode.Atomic::class.java)
    assertThat(config.initialState.state)
      .isEqualTo(Test3States.S3)
  }

  @Test
  fun `given a machine with compound initial state should save it in config`() {
    val config = machine<Test3States, Event, Unit> {
      states {
        initial = Test3States.S2
        machine<Test1States, Unit>(Test3States.S1) {
          addFakeCompoundStates()
        }
        machine<Test1States, Unit>(Test3States.S2) {
          addFakeCompoundStates()
        }
        machine<Test1States, Unit>(Test3States.S3) {
          addFakeCompoundStates()
        }
      }
    }

    assertThat(config.initialState)
      .isInstanceOf(StateNode.Compound::class.java)
    assertThat(config.initialState.state)
      .isEqualTo(Test3States.S2)
  }

  @Test
  fun `given a machine with missing atomic initial state should report an error`() {
    try {
      machine<Test3States, Event, Unit> {
        states {
          initial = Test3States.S1
          state(Test3States.S2) {}
          state(Test3States.S3) {}
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
        states {
          initial = Test3States.S1
          machine<Test1States, Unit>(Test3States.S2) {
            addFakeCompoundStates()
          }
          machine<Test1States, Unit>(Test3States.S3) {
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

  // TODO test that compound and atomic states cannot be mixed on one level

  // endregion
}


private fun <E : Event, C> Machine<Test1States, E, C>.addFakeStates() {
  states {
    initial = Test1States.S1
    state(Test1States.S1) {}
  }
}

private fun <S, C, E : Event, CC> CompoundState<S, C, Test1States, E, CC>.addFakeCompoundStates() {
  states {
    initial = Test1States.S1
    state(Test1States.S1) {}
  }
}

private enum class Test3States {
  S1,
  S2,
  S3
}

private enum class Test1States {
  S1
}
