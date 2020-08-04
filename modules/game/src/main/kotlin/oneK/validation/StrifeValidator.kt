package oneK.validation

import oneK.deck.Card
import oneK.deck.Figure
import oneK.state.State
import oneK.state.currentCardsUnordered
import oneK.state.firstCard
import oneK.variant.Variant

interface StrifeValidator {
    fun canPlay(state: State.Strife, card: Card): Boolean
    fun canTriumph(state: State.Strife, card: Card): Boolean
}

class StrifeStateValidatorImpl(private val variant: Variant) : StrifeValidator {
    override fun canPlay(state: State.Strife, card: Card): Boolean =
        state.currentPlayerHas(card)
                && state.firstCard().let { state.boardIsEmptyOrColorNotPresent(it) || hasMatchingColor(it, card) }

    override fun canTriumph(state: State.Strife, card: Card): Boolean =
        card.figure == Figure.QUEEN
                && state.currentPlayerHas(Card(Figure.KING, card.color))
                && state.currentPlayerHas(card)
                && state.currentCardsUnordered().isEmpty()

    private fun State.Strife.boardIsEmptyOrColorNotPresent(firstCard: Card?) =
        firstCard?.let { first -> !this.order.current().cards.any { it.color == first.color } } ?: true

    private fun hasMatchingColor(firstCard: Card?, card: Card): Boolean = firstCard?.color?.equals(card.color) ?: true

    private fun State.Strife.currentPlayerHas(card: Card) =
        this.order.current().cards.contains(card)
}
