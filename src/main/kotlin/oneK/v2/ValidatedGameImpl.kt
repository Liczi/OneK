package oneK.v2

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.service.*
import oneK.v2.state.*
import oneK.v2.validation.DefaultGameValidator
import oneK.v2.validation.GameValidator
import oneK.v2.variant.DefaultTwoPlayerVariant
import oneK.v2.variant.Variant

//TODO for starters keep games in cache
//or store game variant as enum with default for now
private class ValidatedGameImpl(
    validator: GameValidator,
    private val variant: Variant,
    private val summaryService: SummaryService,
    private val biddingService: BiddingService,
    private val reviewService: ReviewService,
    private val strifeService: StrifeService
) : ValidatedGame(validator), SummaryService by summaryService, BiddingService by biddingService {

//    TODO ????? delete or implement
//    STATE HOLDER (per game - gameUuid, needed for ranking incrementation
    //    read state from memory (should be consistent for all players thus cannot be passed in requests)

//    TODO on the level of server, check if the game is memory effective - if not include Flyweight pattern or state caching - proper hashing function is needed

    override fun doStart(state: State.Summary): State.Bidding {
        val shuffledDeck = getClassicDeck().shuffled()
        return state
            .performStart(shuffledDeck, variant)
            .performBid(variant.getInitialBid())
    }

    override fun doBid(state: State.Bidding, bid: Int): State.Bidding {
        return state.performBid(bid)
    }

    override fun doFold(state: State.Bidding): FoldingEffect {
        state.performFold()
        return if (state.isBiddingCompleted()) {
            state.transitionToReviewState()
        } else {
            FoldingEffect.NoTransition
        }
    }

    override fun doPickTalon(talonIndex: Int): State.Review {
        TODO("Not yet implemented")
    }

    override fun doDistributeCards(toGive: Map<Player, Card>): State.Review {
        TODO("Not yet implemented")
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
}

object GameFactory {
    fun default(): ValidatedGame {
        val variant = DefaultTwoPlayerVariant()
        return ValidatedGameImpl(
            DefaultGameValidator(variant),
            variant,
            DefaultSummaryServiceImpl,
            DefaultBiddingServiceImpl,
            ReviewService,
            StrifeService
        )
    }
}
//TODO possibly extract extensions

private fun State.Strife.transitionToSummaryState(): PlayingEffect.SummaryTransition {
    TODO("Not yet implemented")
}

private fun State.Bidding.transitionToReviewState(): FoldingEffect.ReviewTransition {
//    return FoldingEffect.ReviewTransition(this.biddersOrder.map {  })
    TODO()
}
