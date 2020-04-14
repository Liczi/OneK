package oneK.round.strategy

import oneK.deck.Card
import oneK.deck.Hand

class DefaultVariant : Variant {
    override var getBombPoints: () -> Int = { 60 }
    override var getBombAllowedBidThreshold: () -> Int = { 120 }
    override var setTalonCards: (HashSet<Card>) -> Unit = { cards ->
        require(cards.size == this.getTalonSize() * this.getTalonsQuantity())

        val cardsQuant = this.getTalonSize()
        val talons = this.getTalonsQuantity()
        val range = { ind: Int -> (ind * cardsQuant)..((ind + 1) * cardsQuant - 1) }

        this.getTalons = { Array(talons, { index -> cards.toTypedArray().slice(range(index)).toHashSet() }) }
    }

    override var getTalonSize: () -> Int = { throw UninitializedPropertyAccessException() }
    override var getTalonsQuantity: () -> Int = { throw UninitializedPropertyAccessException() }
    override var getTalons: () -> Array<HashSet<Card>> = { throw UninitializedPropertyAccessException() }
    override var isValid = { _: Hand -> true }
}