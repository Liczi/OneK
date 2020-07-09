package variant

import org.assertj.core.api.Assertions.assertThat
import oneK.v2.toCardSet
import oneK.v2.variant.DefaultTwoPlayerVariant
import oneK.v2.variant.Variant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DefaultVariantTest {

    @Test
    fun `should properly split to default number of talons`() {
        val cards = "9S,9C,9D,9H".toCardSet()
        val variant = DefaultTwoPlayerVariant()

        assertThat(variant.getTalonCards(cards)).containsExactly("9S,9C".toCardSet(), "9D,9H".toCardSet())
    }

    @Test
    fun `should properly split to one talon`() {
        val cards = "9S,9C,9D,9H".toCardSet()
        val variant = Variant.Builder().talonsQuantity(1).build()

        assertThat(variant.getTalonCards(cards)).containsExactly("9S,9C,9D,9H".toCardSet())
    }

    @Test()
    fun `should fail with illegal argument`() {
        val cards = "9S,9C,9D".toCardSet()
        val variant = DefaultTwoPlayerVariant()

        assertThrows<IllegalArgumentException> {variant.getTalonCards(cards)}
    }
}