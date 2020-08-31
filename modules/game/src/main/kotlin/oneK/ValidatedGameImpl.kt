package oneK

import oneK.deck.Card
import oneK.player.Player
import oneK.service.BiddingService
import oneK.service.DefaultBiddingServiceImpl
import oneK.service.DefaultReviewServiceImpl
import oneK.service.DefaultStrifeServiceImpl
import oneK.service.DefaultSummaryServiceImpl
import oneK.service.ReviewService
import oneK.service.StrifeService
import oneK.service.SummaryService
import oneK.state.FoldingEffect
import oneK.state.PlayingEffect
import oneK.state.State
import oneK.state.addPointsAndClearBoard
import oneK.state.isAllCardsPlayed
import oneK.state.isBoardFull
import oneK.state.transitionToReviewState
import oneK.state.transitionToSummaryState
import oneK.validation.DefaultValidator
import oneK.validation.Validator
import oneK.variant.Variant

private class ValidatedGameImpl(
    validator: Validator,
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
        return doStart(
            State.Summary(
                order = state.order.map { it.player },
                ranking = state.ranking
            )
        )
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
    fun default(variant: Variant): ValidatedGame {
        return ValidatedGameImpl(
            DefaultValidator(variant),
            variant,
            DefaultSummaryServiceImpl,
            DefaultBiddingServiceImpl,
            DefaultReviewServiceImpl,
            DefaultStrifeServiceImpl
        )
    }
}