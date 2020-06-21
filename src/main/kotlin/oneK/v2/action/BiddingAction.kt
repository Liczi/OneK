package oneK.v2.action

sealed class BiddingAction {
    data class Bid(val amount: Int): BiddingAction()
    object Fold : BiddingAction()
}