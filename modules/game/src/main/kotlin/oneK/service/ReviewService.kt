package oneK.service

import oneK.deck.Card
import oneK.player.Player
import oneK.state.Reviewer
import oneK.state.State
import oneK.state.Strifer

interface ReviewService {
    fun State.Review.performPickTalon(talonIndex: Int): State.Review
    fun State.Review.performActivateBomb(): State.Review
    fun State.Review.performChangeBid(newBid: Int): State.Review
    fun State.Review.performDistributeCards(toGive: Map<Player, Card>): State.Review
    fun State.Review.performConfirm(): State.Strife
}

internal object DefaultReviewServiceImpl : ReviewService {

    override fun State.Review.performPickTalon(talonIndex: Int): State.Review {
        val talon = this.talon.take(talonIndex)
        return this.copy(
            order = this.order.replaceCurrent { it.copy(cards = it.cards + talon.value) },
            talon = talon
        )
    }

    override fun State.Review.performActivateBomb(): State.Review {
        TODO("Not yet implemented")
    }

    override fun State.Review.performChangeBid(newBid: Int): State.Review {
        return this.copy(changedBid = newBid)
    }

    override fun State.Review.performDistributeCards(toGive: Map<Player, Card>): State.Review {
        return this.copy(
            order = this.order
                .replaceCurrent { it.copy(cards = it.cards - toGive.values) }
                .mapNotCurrent { (cards, player) ->
                    Reviewer(
                        cards = cards + toGive.getValue(player),
                        player = player
                    )
                },
            toGive = toGive
        )
    }

    override fun State.Review.performConfirm(): State.Strife {
        return State.Strife(
            order = this.order
                .map { (cards, player) -> Strifer(cards, player) }
                .replaceCurrent { it.copy(isConstrained = true) },
            bid = this.changedBid ?: this.initialBid,
            ranking = this.ranking
        )
    }
}