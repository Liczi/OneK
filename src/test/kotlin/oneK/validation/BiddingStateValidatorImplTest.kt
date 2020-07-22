package oneK.validation

import oneK.service.DefaultBiddingServiceImpl.performBid
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BiddingStateValidatorImplTest {

    @Nested
    inner class TwoPlayerLimitedBidTest : TestStateHolder.Bidding(TwoPlayer()) {

        @Test
        fun `should not allow invalid bid`() {
            listOf(0, -1, 50, 111, 130).forEach {
                assertNull(validator.canBid(initialState, it))
            }
        }
    }

    @Nested
    inner class TwoPlayerUnlimitedBidTest : TestStateHolder.Bidding(TwoPlayer(), listOf("QC, KC", "QD, KD")) {

        @Test
        fun `should allow bid above 120 if has triumph`() {
            val state = initialState.performBid(110).performBid(120)
            assertNotNull(validator.canBid(state, 130))
            assertNotNull(validator.canBid(state.performBid(130), 140))
        }


        @Test
        fun `should allow not allow bid above 300`() {
            val state = (110..290 step 10)
                .fold(initialState) { state, bid -> state.performBid(bid) }

            assertNotNull(validator.canBid(state, 300))
            assertNull(validator.canBid(state.performBid(300), 310))
        }
    }
}