package oneK.strategy

import oneK.deck.Card

//todo TEST THIS CLASS !!1
class DefaultRoundStrategy : RoundStrategy {
    override var getBombPoints = { 60 }
    override var getBombAllowedBidThreshold = { 120 }
    override var setTalonCards: (HashSet<Card>) -> Unit = { cards ->
        val cardsQuant = this.getTalonSize()
        val talons = this.getTalonsQuantity()
        val range = { ind: Int -> (ind * cardsQuant)..((ind + 1) * cardsQuant - 1) }

        //todo test this
        this.getTalons = { Array(talons, { index -> cards.toTypedArray().slice(range(index)).toHashSet() }) }
    }

    override var getTalonSize: () -> Int = throw UninitializedPropertyAccessException()
    override var getTalonsQuantity: () -> Int = throw UninitializedPropertyAccessException()
    override var getTalons: () -> Array<HashSet<Card>> = throw UninitializedPropertyAccessException()
}