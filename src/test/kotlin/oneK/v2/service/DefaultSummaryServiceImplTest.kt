package service

import oneK.v2.service.DefaultSummaryServiceImpl.performStart
import oneK.v2.state.RepeatableOrder
import oneK.v2.state.State
import oneK.v2.variant.DefaultThreePlayerVariant
import oneK.v2.variant.DefaultTwoPlayerVariant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testsuits.TestStateHolder
import testsuits.ThreePlayer
import testsuits.TwoPlayer

class DefaultSummaryServiceImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Bidding(TwoPlayer()) {
        @Test
        fun `should properly perform start`() {
            val summaryState = State.Summary(RepeatableOrder.of(players).replaceCurrentAndNext())

            val biddingState = summaryState.performStart(getDeck(), DefaultTwoPlayerVariant())

            assertThat(biddingState.biddersOrder.current().player).isEqualTo(players[1])
            assertThat(biddingState.talon).containsExactlyInAnyOrderElementsOf(initialBiddingState.talon)
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Bidding(ThreePlayer()) {
        @Test
        fun `should properly perform start`() {
            val summaryState = State.Summary(RepeatableOrder.of(players).replaceCurrentAndNext().replaceCurrentAndNext())

            val biddingState = summaryState.performStart(getDeck(), DefaultThreePlayerVariant())

            assertThat(biddingState.biddersOrder.current().player).isEqualTo(players[2])
            assertThat(biddingState.talon).containsExactlyInAnyOrderElementsOf(initialBiddingState.talon)
        }
    }
}