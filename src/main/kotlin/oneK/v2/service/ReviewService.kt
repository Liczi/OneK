package oneK.v2.service

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.State

interface ReviewService {
    fun State.Review.performPickTalon(talonIndex: Int): State.Review
    fun State.Review.performActivateBomb(): State.Review
    fun State.Review.performRestart(): State.Review
    fun State.Review.performChangeBid(newBid: Int): State.Review
    fun State.Review.performDistributeCards(toGive: Map<Player, Card>): State.Review
    fun State.Review.performConfirm(): State.Strife
}

internal object DefaultReviewServiceImpl : ReviewService {
    override fun State.Review.performPickTalon(talonIndex: Int): State.Review {
        TODO("Not yet implemented")
    }

    override fun State.Review.performActivateBomb(): State.Review {
        TODO("Not yet implemented")
    }

    override fun State.Review.performRestart(): State.Review {
        TODO("Not yet implemented")
    }

    override fun State.Review.performChangeBid(newBid: Int): State.Review {
        TODO("Not yet implemented")
    }

    override fun State.Review.performDistributeCards(toGive: Map<Player, Card>): State.Review {
        TODO("Not yet implemented")
    }

    override fun State.Review.performConfirm(): State.Strife {
        TODO("Not yet implemented")
    }
}