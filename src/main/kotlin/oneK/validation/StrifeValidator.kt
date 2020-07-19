package oneK.validation

import oneK.deck.Card
import oneK.deck.Figure
import oneK.state.State
import oneK.state.currentCardsUnordered
import oneK.state.firstCard
import oneK.variant.Variant

interface StrifeValidator {
    fun canPlay(state: State.Strife, card: Card): State.Strife?
    fun canTriumph(state: State.Strife, card: Card): State.Strife?
}

class StrifeStateValidatorImpl(private val variant: Variant) : StrifeValidator, StateValidator() {
    override fun canPlay(state: State.Strife, card: Card): State.Strife? {
        return state.ensureValid {
            state.currentPlayerHas(card)
                    && state.firstCard().let { state.boardIsEmptyOrColorNotPresent(it) || hasMatchingColor(it, card) }
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

    private fun State.Strife.boardIsEmptyOrColorNotPresent(firstCard: Card?) =
        firstCard?.let { first -> !this.order.current().cards.any { it.color == first.color } } ?: true

    private fun hasMatchingColor(firstCard: Card?, card: Card): Boolean = firstCard?.color?.equals(card.color) ?: true

    private fun Figure.mate() = if (this == Figure.KING) Figure.QUEEN else Figure.KING

    private fun State.Strife.currentPlayerHas(card: Card) =
        this.order.current().cards.contains(card)
}
