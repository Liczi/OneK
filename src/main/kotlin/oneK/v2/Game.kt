package oneK.v2

import oneK.deck.Card
import oneK.v2.service.*
import oneK.v2.state.*
import oneK.v2.validation.DefaultGameValidator
import oneK.v2.validation.GameValidator
import oneK.v2.variant.DefaultVariant
import oneK.v2.variant.Variant

//TODO for starters keep games in cache
//or store game variant as enum with default for now
class Game private constructor(
        gameState: GameState,
        variant: Variant,
        validator: GameValidator,
        private val summaryService: SummaryService, //todo delegate these???
        private val biddingService: BiddingServiceImpl,
        private val reviewService: ReviewService,
        private val strifeService: StrifeService
) : ValidatedGame(gameState, variant, validator), BiddingService by biddingService {

//    STATE HOLDER (per game - gameUuid, needed for ranking incrementation
    //    read state from memory (should be consistent for all players thus cannot be passed in requests)
//    TODO summaryState here (initial with ranking - 0)
//    players: List<Player>, variant: Variant

    override fun doStart(): State.Bidding {
        TODO()
//        initialize state, transition to bidding and return state
//        Bidding - first player has already bidded 100
    }

    override fun doBid(bid: Int): State.Bidding {
        return this.gameState.biddingState.bid(bid)
    }

    override fun doFold(): FoldingEffect { // general state (could be either bidding or review
        val state = this.gameState.biddingState.fold()
        return if (state.isBiddingCompleted()) {
            state.transitionToReviewState()
        } else {
            FoldingEffect.NoTransition
        }
    }

    override fun doActivateBomb(): State.Summary {
        TODO()
    }

    override fun doRestart(state: State.Bidding): State.Bidding {
        TODO("Not yet implemented")
    }

    override fun doChangeBid(newBid: Int): State.Review {
        TODO()
    }

    override fun doConfirmBid(): State.Strife {
        TODO()
    }

    override fun doPlay(card: Card): PlayingEffect {
//                return PlayingEffect.StrifeTransition(StrifeState())
        TODO()
    }

    override fun doTriumph(card: Card): State.Strife {
        TODO()
    }

    class Factory {
        fun default(gameState: GameState): ValidatedGame {
            return Game(
                    gameState,
                    DefaultVariant(),
                    DefaultGameValidator,
                    SummaryService,
                    BiddingServiceImpl,
                    ReviewService,
                    StrifeService
            )
        }
    }
}

//TODO possibly extract extensions

private fun State.Bidding.isBiddingCompleted() = this.bidders.filter(Bidder::folded).size <= 1

private fun State.Strife.transitionToSummaryState(): PlayingEffect {
    TODO("Not yet implemented")
}

private fun State.Bidding.transitionToReviewState(): FoldingEffect {
    TODO("Not yet implemented")
}
