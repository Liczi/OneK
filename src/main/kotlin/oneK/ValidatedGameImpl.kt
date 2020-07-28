package oneK

import oneK.deck.Card
import oneK.player.Player
import oneK.service.*
import oneK.state.*
import oneK.validation.DefaultGameValidator
import oneK.validation.GameValidator
import oneK.variant.DefaultTwoPlayerVariant
import oneK.variant.Variant

private class ValidatedGameImpl(
    validator: GameValidator,
    private val variant: Variant,
    private val summaryService: SummaryService,
    private val biddingService: BiddingService,
    private val reviewService: ReviewService,
    private val strifeService: StrifeService
) : ValidatedGame(validator), SummaryService by summaryService, BiddingService by biddingService,
    ReviewService by reviewService, StrifeService by strifeService {

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
        return state
            .performFold()
            .let {
                if (it.isAllCardsPlayed()) {
                    it.transitionToReviewState()
                } else {
                    FoldingEffect.NoTransition(it)
                }
            }
    }

    override fun doPickTalon(state: State.Review, talonIndex: Int): State.Review {
        return state.performPickTalon(talonIndex)
    }

    override fun doDistributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review {
        return state.performDistributeCards(toGive)
    }

    override fun doActivateBomb(state: State.Review): State.Summary {
        TODO("Not yet implemented")
    }

    override fun doRestart(state: State.Review): State.Bidding {
        return state.performRestart()
    }

    override fun doChangeBid(state: State.Review, newBid: Int): State.Review {
        return state.performChangeBid(newBid)
    }

    override fun doConfirm(state: State.Review): State.Strife {
        return state.performConfirm()
    }

    override fun doPlay(state: State.Strife, card: Card): PlayingEffect {
        return state
            .performPlay(card)
            .let {
                when {
                    it.isAllCardsPlayed() -> it.addPointsAndClearBoard().transitionToSummaryState()
                    it.isBoardFull() -> PlayingEffect.NoTransition(it.addPointsAndClearBoard())
                    else -> PlayingEffect.NoTransition(it)
                }
            }
    }

    override fun doTriumph(state: State.Strife, card: Card): State.Strife {
        return state.performTriumph(card)
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
            DefaultReviewServiceImpl,
            DefaultStrifeServiceImpl
        )
    }
}