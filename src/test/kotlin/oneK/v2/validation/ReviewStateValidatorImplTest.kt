package validation

import oneK.v2.service.DefaultReviewServiceImpl.performDistributeCards
import oneK.v2.service.DefaultReviewServiceImpl.performPickTalon
import oneK.v2.toCardSet
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testsuits.TestStateHolder
import testsuits.TwoPlayer

internal class ReviewStateValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Review(TwoPlayer()) {

        private val toGive = players.filter { it != players[0] }.zip("9C".toCardSet()).toMap()

        @Test
        fun `should block actions in initial state`() {
            assertNull(validator.canChangeBid(initialState, 120))
            assertNull(validator.canConfirm(initialState))
            assertNull(validator.canDistributeCards(initialState, toGive))
        }

        @Test
        fun `should allow to take talon in initial state`() {
            (0..1).forEach {
                assertNotNull(validator.canPickTalon(initialState, it))
            }
        }

        @Test
        fun `should allow to distribute cards`() {
            val state = initialState.performPickTalon(0)

            assertNotNull(validator.canDistributeCards(state, toGive))
        }

        @Test
        fun `should not allow to distribute cards which are not owned`() {
            val state = initialState.performPickTalon(0)

            val invalidToGive = toGive.keys.zip("KC".toCardSet()).toMap()
            assertNull(validator.canDistributeCards(state, invalidToGive))
        }

        @Test
        fun `should allow performing confirm`() {
            val state = initialState
                .performPickTalon(0)
                .performDistributeCards(toGive)

            assertNotNull(validator.canConfirm(state))
        }
    }
}