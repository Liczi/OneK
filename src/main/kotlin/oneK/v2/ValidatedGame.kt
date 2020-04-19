package oneK.v2

import oneK.deck.Card
import oneK.v2.state.*
import oneK.v2.validation.GameValidator
import oneK.v2.variant.Variant

abstract class ValidatedGame(
        protected val gameState: GameState,
        protected val variant: Variant,
        private val validator: GameValidator
) {
    protected abstract fun doStart(): State.Bidding
    protected abstract fun doBid(bid: Int): State.Bidding
    protected abstract fun doFold(): FoldingEffect
    protected abstract fun doActivateBomb(): State.Summary
    protected abstract fun doRestart(state: State.Bidding): State.Bidding
    protected abstract fun doChangeBid(newBid: Int): State.Review
    protected abstract fun doConfirmBid(): State.Strife
    protected abstract fun doPlay(card: Card): PlayingEffect
    protected abstract fun doTriumph(card: Card): State.Strife

    //    TODO validate and do*
    fun start(): State.Bidding {
        TODO()
    }

    fun bid(bid: Int) {
        TODO()
    }

    fun fold(): FoldingEffect {
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
}
