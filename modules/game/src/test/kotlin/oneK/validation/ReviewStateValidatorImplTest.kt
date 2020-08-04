package oneK.validation

import oneK.asCardSet
import oneK.service.DefaultReviewServiceImpl.performDistributeCards
import oneK.service.DefaultReviewServiceImpl.performPickTalon
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ReviewStateValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Review(TwoPlayer()) {

        private val toGive = players.filter { it != players[0] }.zip("9C".asCardSet()).toMap()

        @Test
        fun `should block actions in initial state`() {
            assertFalse(validator.canChangeBid(initialState, 120))
            assertFalse(validator.canConfirm(initialState))
            assertFalse(validator.canDistributeCards(initialState, toGive))
        }

        @Test
        fun `should allow to take talon in initial state`() {
            (0..1).forEach {
                assertTrue(validator.canPickTalon(initialState, it))
            }
        }

        @Test
        fun `should allow to distribute cards`() {
            val state = initialState.performPickTalon(0)

            assertTrue(validator.canDistributeCards(state, toGive))
        }

        @Test
        fun `should not allow to distribute cards which are not owned`() {
            val state = initialState.performPickTalon(0)

            val invalidToGive = toGive.keys.zip("KC".asCardSet()).toMap()
            assertFalse(validator.canDistributeCards(state, invalidToGive))
        }

        @Test
        fun `should allow performing confirm`() {
            val state = initialState
                .performPickTalon(0)
                .performDistributeCards(toGive)

            assertTrue(validator.canConfirm(state))
        }

        @Test
        fun `should not allow performing restart`() {
            val state = initialState
                .performPickTalon(0)

            assertFalse(validator.canRestart(state))
        }
    }

    @Nested
    inner class TwoPlayerRestartTest : TestStateHolder.Review(TwoPlayer(), listOf("9H, 9D", "TC, TD")) {

        @Test
        fun `should not allow performing restart`() {
            val state = initialState
                .performPickTalon(0)

            assertTrue(validator.canRestart(state))
        }
    }
}