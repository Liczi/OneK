package oneK.validation

import oneK.deck.Card
import oneK.service.DefaultStrifeServiceImpl.performPlay
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer

internal class StrifeStateValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Strife(TwoPlayer(), listOf("KC, QC, AC, 9H", "AD, QD, KD, 9C")) {

        @Test
        fun `should not allow playing a card which is not present`() {
            assertNull(validator.canPlay(initialState, Card.fromString('A', 'D')))
        }

        @Test
        fun `should not allow triumph if no mate present`() {
            assertNull(validator.canTriumph(initialState, Card.fromString('Q', 'D')))
        }

        @Test
        fun `should allow playing a card which is not present`() {
            assertNotNull(validator.canPlay(initialState, Card.fromString('A', 'C')))
        }

        @Test
        fun `should allow triumph if no mate present`() {
            assertNotNull(validator.canTriumph(initialState, Card.fromString('Q', 'C')))
        }

        @Test
        fun `should not allow playing off color`() {
            val newState = initialState
                .performPlay(Card.fromString('Q', 'C'))

            assertNull(validator.canPlay(newState, Card.fromString('Q', 'D')))
        }

        @Test
        fun `should allow playing off color`() {
            val newState = initialState
                .performPlay(Card.fromString('9', 'H'))

            assertNotNull(validator.canPlay(newState, Card.fromString('Q', 'D')))
        }
    }

}