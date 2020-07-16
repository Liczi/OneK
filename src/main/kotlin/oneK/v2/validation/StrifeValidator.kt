package oneK.v2.validation

import oneK.deck.Card
import oneK.deck.Figure
import oneK.v2.state.State
import oneK.v2.state.currentCardsUnordered
import oneK.v2.state.firstCard
import oneK.v2.variant.Variant

interface StrifeValidator {
    fun canPlay(state: State.Strife, card: Card): State.Strife?
    fun canTriumph(state: State.Strife, card: Card): State.Strife?
}

class StrifeStateValidatorImpl(private val variant: Variant) : StrifeValidator, StateValidator() {
    override fun canPlay(state: State.Strife, card: Card): State.Strife? {
        return state.ensureValid {
            state.currentPlayerHas(card)
                    && state.hasNoCardsOrMatchesColor(card)
        }
    }

    override fun canTriumph(state: State.Strife, card: Card): State.Strife? {
        return state.ensureValid {
            state.currentPlayerHas(card)
                    && (card.figure == Figure.KING || card.figure == Figure.QUEEN)
                    && state.currentPlayerHas(Card(card.figure.mate(), card.color))
                    && state.currentCardsUnordered().isEmpty()
        }
    }

    private fun State.Strife.hasNoCardsOrMatchesColor(card: Card) =
        this.firstCard()?.let { it.color == card.color } ?: true

    private fun Figure.mate() = if (this == Figure.KING) Figure.QUEEN else Figure.KING

    private fun State.Strife.currentPlayerHas(card: Card) =
        this.order.current().cards.contains(card)
}
