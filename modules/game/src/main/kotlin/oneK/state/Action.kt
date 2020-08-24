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
        data class Distribute(val toGive: Map<Player, Card>) : Review()
        object Restart : Review()
        object Confirm : Review()
    }

    sealed class Strife(val card: Card) : Action() {
        class Play(card: Card) : Strife(card)
        class Triumph(card: Card) : Strife(card)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return when (this) {
            is Bidding.Bid -> (other as Bidding.Bid).amount == this.amount
            is Review.Pick -> (other as Review.Pick).talonInd == this.talonInd
            is Review.Change -> (other as Review.Change).newBid == this.newBid
            is Review.Distribute -> (other as Review.Distribute).toGive == this.toGive
            is Strife.Play -> (other as Strife.Play).card == this.card
            is Strife.Triumph -> (other as Strife.Triumph).card == this.card
            else -> true
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}