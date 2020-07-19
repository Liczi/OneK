package oneK.service

import oneK.deck.Card
import oneK.service.DefaultStrifeServiceImpl.performPlay
import oneK.service.DefaultStrifeServiceImpl.performTriumph
import oneK.state.StrifeAction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer

internal class DefaultStrifeServiceImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Strife(TwoPlayer(), listOf("KC, QC", "JC, QD")) {

        @Test
        fun `should perform play card properly`() {
            val card = Card.fromString('K', 'C')
            val newState = initialState.performPlay(card)

            val previous = newState.order.previous().current()
            assertThat(previous.cards).doesNotContain(card)
            assertThat((previous.lastAction as StrifeAction.Play).card).isEqualTo(card)
        }

        @Test
        fun `should perform triumph properly`() {
            val card = Card.fromString('K', 'C')
            val newState = initialState.performTriumph(card)

            val previous = newState.order.previous().current()
            assertThat(previous.cards).doesNotContain(card)
            assertThat((previous.lastAction as StrifeAction.Triumph).card).isEqualTo(card)
            assertThat(previous.points).isEqualTo(card.color.value)
        }
    }
}