package oneK.v2.variant

import oneK.deck.Card
import oneK.deck.Figure
import oneK.deck.Hand
import oneK.game.MAXIMUM_BID

class DefaultVariant : Variant {
    override var getUpperBidThreshold = { MAXIMUM_BID }
    override var getInitialBid = { 100 }
    override var getMaxBidStep = { 10 }
    override var canBid: (Set<Card>, Int) -> Boolean = { cards, bid ->
        bid <= 120 || cards.hasTriumph()
    }
    override var getLimitedScoringThreshold = { 900 }
    override var getBombPoints: () -> Int = { 60 }
    override var getBombAllowedBidThreshold: () -> Int = { 120 }
//    TODO make this function return talons, and store in state, delete get methods for talon
    override var setTalonCards: (HashSet<Card>) -> Unit = { cards ->
        require(cards.size == this.getTalonSize() * this.getTalonsQuantity())

        val cardsQuant = this.getTalonSize()
        val talons = this.getTalonsQuantity()
        val range = { ind: Int -> (ind * cardsQuant) until (ind + 1) * cardsQuant }

        this.getTalons = { Array(talons) { index -> cards.toTypedArray().slice(range(index)).toHashSet() } }
    }

    override var getTalonSize: () -> Int = { throw UninitializedPropertyAccessException() }
    override var getTalonsQuantity: () -> Int = { throw UninitializedPropertyAccessException() }
    override var getTalons: () -> Array<HashSet<Card>> = { throw UninitializedPropertyAccessException() }
    override var qualifiesForRestart = { _: Hand -> false }
}

//TODO extract to file
fun Set<Card>.hasTriumph(): Boolean {
    val kings = this.filter { it.figure == Figure.KING }
    val queens = this.filter { it.figure == Figure.QUEEN }

    return kings.any { queens.map { it.color }.contains(it.color) }
}