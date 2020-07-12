package oneK.v2.state

import oneK.deck.Card

sealed class BiddingAction {
    data class Bid(val amount: Int): BiddingAction()
    object Fold : BiddingAction()
}

sealed class StrifeAction {
    data class Play(val card: Card): StrifeAction()
    data class Triumph(val card: Card): StrifeAction()
}