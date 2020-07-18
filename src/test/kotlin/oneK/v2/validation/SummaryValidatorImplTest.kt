package validation

import oneK.v2.state.State
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testsuits.TestStateHolder
import testsuits.ThreePlayer
import testsuits.TwoPlayer

internal class SummaryValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Summary(TwoPlayer()) {

        @Test
        fun `should allow start for initial summary state`() {
            assertNotNull(validator.canStart(initialState))
        }

        @Test
        fun `should disallow start gor a finished game`() {
            val ranking = initialState.ranking.mapValues { it.value + this.players.indexOf(it.key) * 1000 }
            assertNull(validator.canStart(State.Summary(initialState.order, ranking)))
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Summary(ThreePlayer()) {

        @Test
        fun `should allow start for initial summary state`() {
            assertNotNull(validator.canStart(initialState))
        }

        @Test
        fun `should disallow start gor a finished game`() {
            val ranking = initialState.ranking.mapValues { it.value + this.players.indexOf(it.key) * 500 }
            assertNull(validator.canStart(State.Summary(initialState.order, ranking)))
        }
    }
}