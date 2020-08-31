package oneK.testsuits

import oneK.asCardSet
import oneK.deck.Card
import oneK.player.Player
import oneK.state.Action
import oneK.state.Bidder
import oneK.state.Choice
import oneK.state.RepeatableOrder
import oneK.state.Reviewer
import oneK.state.State
import oneK.state.Strifer
import oneK.validation.BiddingStateValidatorImpl
import oneK.validation.BiddingValidator
import oneK.validation.ReviewStateValidatorImpl
import oneK.validation.ReviewValidator
import oneK.validation.StrifeStateValidatorImpl
import oneK.validation.SummaryStateValidatorImpl

abstract class TestStateHolder(
    cards: List<String>? = null,
    talon: List<String>? = null
) {

    private val _singleTalon = (talon ?: listOf("9S,9C,9D")).map(String::asCardSet)
    private val _doubleTalon = (talon ?: listOf("9S,9C", "9D,9H")).map(String::asCardSet)
    private val _cards = (cards ?: listOf("JS,JC", "JD,JH", "QS,QC")).map(String::asCardSet)

    protected fun getPlayerCards(players: List<Player>): List<Pair<Player, Set<Card>>> = players.zip(_cards)

    protected fun getTalon(players: List<Player>): List<Set<Card>> =
        if (players.size == 2) _doubleTalon else _singleTalon

    abstract class Summary(
        private val playersHolder: TestPlayersHolder,
        cards: List<String>? = null,
        talon: List<String>? = null
    ) : TestStateHolder(cards, talon),
        TestPlayersHolder by playersHolder {

        protected val validator = SummaryStateValidatorImpl(variant)
        private val order = RepeatableOrder.of(this.players)
        protected val initialState = State.Summary(order)
    }

    abstract class Bidding(
        private val playersHolder: TestPlayersHolder,
        cards: List<String>? = null,
        talon: List<String>? = null
    ) : TestStateHolder(cards, talon),
        TestPlayersHolder by playersHolder {

        protected val validator: BiddingValidator = BiddingStateValidatorImpl(variant)

        private val playerCards = getPlayerCards(this.players)
        private val talon = getTalon(this.players)

        protected val initialState = State.Bidding(
            order = RepeatableOrder.of(
                order = playerCards.map { (player, cardsString) -> Bidder(cardsString, player) }
            ).replaceCurrentAndNext { it.copy(lastAction = Action.Bidding.Bid(100)) },
            talon = this.talon,
            ranking = this.players.associateWith { 0 }
        )

        protected fun getDeck(): List<Card> =
            (playerCards.map { it.second } + talon)
                .flatten()
                .sortedBy { it.figure.value * 100 + it.color.value }

    }

    abstract class Review(
        private val playersHolder: TestPlayersHolder,
        cards: List<String>? = null,
        talon: List<String>? = null
    ) : TestStateHolder(cards, talon),
        TestPlayersHolder by playersHolder {

        protected val validator: ReviewValidator = ReviewStateValidatorImpl(variant)
        protected val initialState = State.Review(
            order = RepeatableOrder.of(
                getPlayerCards(this.players).map { (player, cardsString) ->
                    Reviewer(
                        cardsString,
                        player
                    )
                }
            ),
            initialBid = 100,
            talon = Choice.of(getTalon(this.players)),
            ranking = this.players.associateWith { 0 }
        )
    }


    abstract class Strife(
        private val playersHolder: TestPlayersHolder,
        cards: List<String>? = null,
        talon: List<String>? = null
    ) : TestStateHolder(cards, talon),
        TestPlayersHolder by playersHolder {

        protected val validator = StrifeStateValidatorImpl(variant)
        protected val initialState = State.Strife(
            order = RepeatableOrder.of(
                getPlayerCards(this.players).map { (player, cardsString) ->
                    Strifer(cardsString, player)
                }
            ).replaceCurrent { it.copy(isConstrained = true) },
            bid = 100,
            ranking = this.players.associateWith { 0 }
        )
    }
}