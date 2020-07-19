package oneK.service

import oneK.service.DefaultSummaryServiceImpl.performStart
import oneK.state.RepeatableOrder
import oneK.state.State
import oneK.variant.DefaultThreePlayerVariant
import oneK.variant.DefaultTwoPlayerVariant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.testsuits.TwoPlayer

internal class DefaultSummaryServiceImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Bidding(TwoPlayer()) {

        @Test
        fun `should properly perform start`() {
            val summaryState = State.Summary(RepeatableOrder.of(players).next())

            val biddingState = summaryState.performStart(getDeck(),
                DefaultTwoPlayerVariant()
            )

            assertThat(biddingState.order.current().player).isEqualTo(players[1])
            assertThat(biddingState.talon).containsExactlyInAnyOrderElementsOf(initialState.talon)
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Bidding(ThreePlayer()) {

        @Test
        fun `should properly perform start`() {
            val summaryState = State.Summary(RepeatableOrder.of(players).next().next())

            val biddingState = summaryState.performStart(getDeck(),
                DefaultThreePlayerVariant()
            )

            assertThat(biddingState.order.current().player).isEqualTo(players[2])
            assertThat(biddingState.talon).containsExactlyInAnyOrderElementsOf(initialState.talon)
        }
    }
}