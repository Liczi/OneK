package oneK.v2.state

import oneK.deck.Card

sealed class BiddingAction {
    data class Bid(val amount: Int) : BiddingAction()
    object Fold : BiddingAction()
}

sealed class StrifeAction(val card: Card) {
    class Play(card: Card) : StrifeAction(card)
    class Triumph(card: Card) : StrifeAction(card)
}