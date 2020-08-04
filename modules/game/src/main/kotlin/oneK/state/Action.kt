package oneK.state

import oneK.deck.Card
import oneK.player.Player

sealed class Action {

    sealed class Summary : Action() {
        object Start : Summary()
    }

    sealed class Bidding : Action() {
        data class Bid(val amount: Int) : Bidding()
        object Fold : Bidding()
    }

    sealed class Review : Action() {
        data class Pick(val talonInd: Int) : Review()
        data class Change(val newBid: Int) : Review()
        data class Distribute(val toGive: Map<Player, Card>): Review()
        object Restart : Review()
        object Confirm : Review()
    }

    sealed class Strife(val card: Card) : Action() {
        class Play(card: Card) : Strife(card)
        class Triumph(card: Card) : Strife(card)
    }
}