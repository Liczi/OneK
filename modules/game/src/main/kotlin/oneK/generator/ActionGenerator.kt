package oneK.generator

import oneK.deck.Card
import oneK.deck.Figure
import oneK.player.Player
import oneK.state.Action
import oneK.state.Choice
import oneK.state.State
import oneK.state.currentBid
import oneK.validation.Validator
import oneK.variant.Variant

class ActionGenerator(
    private val validator: Validator,
    private val variant: Variant
) {

    fun generate(state: State.Summary): List<Action.Summary> =
        if (validator.canStart(state)) listOf(Action.Summary.Start)
        else emptyList()

    fun generate(state: State.Bidding): List<Action.Bidding> =
        listOfNotNull(
            (state.currentBid() + variant.getBidStep())
                .takeIf { validator.canBid(state, it) }
                ?.let { Action.Bidding.Bid(it) },
            Action.Bidding.Fold
        )

    fun generate(state: State.Review): List<Action.Review> =
        when {
            state.talon is Choice.NotTaken -> generatePickTalon(state)
            state.toGive == null -> generateDistributeOrRestart(state)
            state.changedBid == null -> generateChangeOrConfirm(state)
            else -> listOf(Action.Review.Confirm)
        }

    fun generate(state: State.Strife): List<Action.Strife> {
        val cards = state.order.current().cards
        val play = cards
            .filter { validator.canPlay(state, it) }
            .map { Action.Strife.Play(it) }
        val triumph = cards
            .filter { it.figure == Figure.QUEEN }
            .filter { validator.canTriumph(state, it) }
            .map { Action.Strife.Triumph(it) }
        return play + triumph
    }

    private fun generatePickTalon(state: State.Review): List<Action.Review.Pick> {
        return state.talon.indices
            .filter { validator.canPickTalon(state, it) } //todo validate in tests not here
            .map { Action.Review.Pick(it) }
    }

    private fun generateDistributeOrRestart(state: State.Review): List<Action.Review> {
        val cards = state.order.current().cards
        val others = (state.order - state.order.current()).map { it.player }
        return generatePlayerToCardMappings(others, cards)
            .map { Action.Review.Distribute(it) }
            .appendRestartIfPossible(state)
        //                TODO not validating here - do in testing
    }

    private fun generatePlayerToCardMappings(
        others: List<Player>,
        cards: Set<Card>
    ): List<Map<Player, Card>> =
        when (others.size) {
            1 -> {
                val other = others.first()
                cards.map { mapOf(other to it) }
            }
            2 -> cards.flatMap { card -> (cards - card).map { others.zip(listOf(card, it)).toMap() } }
            else -> throw NotImplementedError("This variant is not implemented in the generator")
        }

    private fun List<Action.Review.Distribute>.appendRestartIfPossible(state: State.Review) =
        this.let { if (validator.canRestart(state)) it + Action.Review.Restart else it }

    private fun generateChangeOrConfirm(state: State.Review): List<Action.Review> {
        return biddingRange(state.initialBid)
            .filter { validator.canChangeBid(state, it) }
            .map { Action.Review.Change(it) } + Action.Review.Confirm
    }

    private fun biddingRange(initialBid: Int): IntProgression {
        val maxBid = variant.getUpperBidThreshold()
        val bidStep = variant.getBidStep()
        val from = initialBid + bidStep

        return from..maxBid step bidStep
    }
}
