package oneK.validation

import oneK.asCard
import oneK.service.DefaultStrifeServiceImpl.performPlay
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StrifeStateValidatorImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Strife(TwoPlayer(), listOf("KC, QC, AC, 9H", "AD, QD, KD, 9C")) {

        @Test
        fun `should not allow playing a card which is not present`() {
            assertNull(validator.canPlay(initialState, "AD".asCard()))
        }

        @Test
        fun `should not allow triumph if no mate present`() {
            assertNull(validator.canTriumph(initialState, "QD".asCard()))
        }

        @Test
        fun `should allow playing a card which is not present`() {
            assertNotNull(validator.canPlay(initialState, "AC".asCard()))
        }

        @Test
        fun `should allow triumph if no mate present`() {
            assertNotNull(validator.canTriumph(initialState, "QC".asCard()))
        }

        @Test
        fun `should not allow playing off color`() {
            val newState = initialState
                .performPlay("QC".asCard())

            assertNull(validator.canPlay(newState, "QD".asCard()))
        }

        @Test
        fun `should allow playing off color`() {
            val newState = initialState
                .performPlay("9H".asCard())

            assertNotNull(validator.canPlay(newState, "QD".asCard()))
        }
    }

}