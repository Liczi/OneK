package oneK.validation

import oneK.state.BiddingAction
import oneK.toCardSet
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer

internal class BiddingStateValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Bidding(TwoPlayer()) {

        @Test
        fun `should not allow invalid bid`() {
            listOf(0, -1, 50, 111, 130).forEach {
                assertNull(validator.canBid(initialState, it))
            }
        }

        @Test
        fun `should allow bid above 120 if has triumph`() {
            val state =
                initialState.copy(
                    order = initialState.order.replaceCurrent(
                        initialState.order.current().copy(
                            cards = "QC, KC".toCardSet(),
                            lastAction = BiddingAction.Bid(120)
                        )
                    )
                )

            assertNotNull(validator.canBid(state, 130))
        }
    }
}