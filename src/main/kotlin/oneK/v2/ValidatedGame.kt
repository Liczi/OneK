package oneK.v2

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.*
import oneK.v2.validation.GameValidator

abstract class ValidatedGame(
        private val validator: GameValidator
) {
    protected abstract fun doStart(state: State.Summary): State.Bidding

    protected abstract fun doBid(state: State.Bidding, bid: Int): State.Bidding
    protected abstract fun doFold(state: State.Bidding): FoldingEffect

    protected abstract fun doPickTalon(talonInd: Int): State.Review
    protected abstract fun doDistributeCards(toGive: Map<Player, Card>)
    protected abstract fun doActivateBomb(): State.Summary
    protected abstract fun doRestart(state: State.Bidding): State.Bidding
    protected abstract fun doChangeBid(newBid: Int): State.Review
    protected abstract fun doConfirmBid(): State.Strife

    protected abstract fun doPlay(card: Card): PlayingEffect
    protected abstract fun doTriumph(card: Card): State.Strife

    //    TODO validate state and call can* function if exists then call do*
    fun start(state: State.Summary): State.Bidding =
            validator.canStart(state)
                    ?.let { doStart(state) }
                    ?: illegalState(state)

    fun bid(state: State.Bidding, bid: Int): State.Bidding =
            validator.canBid(bid, state)
                    ?.let { doBid(it, bid) }
                    ?: illegalState(state)

    fun fold(state: State.Bidding): FoldingEffect = doFold(state)

    fun pickTalon(talonInd: Int): State.Review {
        TODO()
    }

    fun distributeCards(toGive: Map<Player, Card>): State.Review {
        TODO()
    }

    fun activateBomb(): State.Summary {
        TODO()
    }

    fun restart(state: State.Bidding): State.Bidding {
        TODO()
    }

    fun changeBid(newBid: Int): State.Review {
        TODO()
    }

    fun confirmBid(): State.Strife {
        TODO()
    }

    fun play(card: Card): PlayingEffect {
        TODO()
    }

    fun triumph(card: Card): State.Strife {
        TODO()
    }

    private fun <T> illegalState(state: State): T {
        throw IllegalStateException("Illegal action on state: $state")
    }
}
