package service

import oneK.v2.service.TestStateHolder
import oneK.v2.service.DefaultSummaryServiceImpl.performStart
import oneK.v2.service.TestPlayersHolder
import oneK.v2.state.State
import oneK.v2.variant.DefaultVariant
import oneK.v2.variant.Variant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DefaultSummaryServiceImplTest {

    @Nested
    inner class TwoPlayer : TestStateHolder.Bidding(TestPlayersHolder.TwoPlayer()) {
        @Test
        fun `should properly perform start`() {
            val summaryState = State.Summary(listOf(getPlayers()[1], getPlayers()[0]))

            val biddingState = summaryState.performStart(getDeck(), DefaultVariant())

            assertThat(biddingState.biddersOrder.current()).isEqualTo(initialBiddingState.biddersOrder[0])
            assertThat(biddingState.talon).containsExactlyInAnyOrderElementsOf(initialBiddingState.talon)
        }
    }

    @Nested
    inner class ThreePlayer : TestStateHolder.Bidding(TestPlayersHolder.ThreePlayer()) {
        @Test
        fun `should properly perform start`() {
            val summaryState = State.Summary(listOf(getPlayers()[2], getPlayers()[0], getPlayers()[1]))

//            TODO extract to constant
            val variant = Variant.Builder().talonsQuantity(1).talonCardsQuantity(3).build()
            val biddingState = summaryState.performStart(getDeck(), variant)

            assertThat(biddingState.biddersOrder.current()).isEqualTo(initialBiddingState.biddersOrder[0])
            assertThat(biddingState.talon).containsExactlyInAnyOrderElementsOf(initialBiddingState.talon)
        }
    }
}