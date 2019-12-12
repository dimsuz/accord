package ru.dimsuz.accord

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.IllegalStateException

class DslConfigurationTest {

  // region BasicConfig

  @Test
  fun `given missing machine id should generate a default one`() {
    val config = machine<TestStates, Event, Unit> { }
    assertThat(config.id)
      .isNotEmpty()
  }

  @Test
  fun `given non-empty machine id should use it`() {
    val config = machine<TestStates, Event, Unit> {
      id = "my-id"
    }
    assertThat(config.id)
      .isEqualTo("my-id")
  }

  @Test
  fun `given empty machine id should throw config exception`() {
    try {
      machine<TestStates, Event, Unit> {
        id = ""
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
      machine<TestStates, Event, Unit> {
        id = "    "
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
    val config = machine<TestStates, Event, List<Int>> {
      context = arrayListOf(1, 3, 3)
    }

    assertThat(config.context)
      .isEqualTo(arrayListOf(1, 3, 3))
  }

  // endregion

  // region States

  // endregion
}

private enum class TestStates {
  S1,
  S2,
  S3
}
