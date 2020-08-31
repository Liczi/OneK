package oneK.validation

import oneK.state.State
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.testsuits.TwoPlayer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SummaryValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Summary(TwoPlayer()) {

        @Test
        fun `should allow start for initial summary state`() {
            assertTrue(validator.canStart(initialState))
        }

        @Test
        fun `should disallow start for a finished game`() {
            val ranking = initialState.ranking
                .mapValues { it.value + this.players.indexOf(it.key) * variant.getGameGoal() }
            assertFalse(
                validator.canStart(
                    State.Summary(
                        order = initialState.order,
                        ranking = ranking
                    )
                )
            )
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Summary(ThreePlayer()) {

        @Test
        fun `should allow start for initial summary state`() {
            assertTrue(validator.canStart(initialState))
        }

        @Test
        fun `should disallow start for a finished game`() {
            val ranking = initialState.ranking.mapValues { it.value + this.players.indexOf(it.key) * 500 }
            assertFalse(
                validator.canStart(
                    State.Summary(
                        order = initialState.order,
                        ranking = ranking
                    )
                )
            )
        }
    }
}