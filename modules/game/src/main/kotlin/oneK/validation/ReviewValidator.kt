package oneK.validation

import oneK.containsAllNines
import oneK.deck.Card
import oneK.player.Player
import oneK.state.Choice
import oneK.state.Reviewer
import oneK.state.State
import oneK.state.isValidBid
import oneK.variant.Variant

interface ReviewValidator {
    fun canPickTalon(state: State.Review, talonIndex: Int): Boolean
    fun canActivateBomb(state: State.Review): Boolean
    fun canRestart(state: State.Review): Boolean
    fun canChangeBid(state: State.Review, newBid: Int): Boolean
    fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): Boolean
    fun canConfirm(state: State.Review): Boolean
}

internal class ReviewStateValidatorImpl(private val variant: Variant) : ReviewValidator {

    override fun canPickTalon(state: State.Review, talonIndex: Int): Boolean =
        state.talon is Choice.NotTaken
                && talonIndex < state.talon.size
                && talonIndex >= 0

    override fun canActivateBomb(state: State.Review): Boolean {
        TODO("Not yet implemented")
    }

    override fun canRestart(state: State.Review): Boolean =
        state.talon is Choice.Taken
                && state.toGive == null
                && state.order.current().cards.containsAllNines()

    override fun canChangeBid(state: State.Review, newBid: Int): Boolean =
        state.changedBid == null
                && state.talon is Choice.Taken
                && state.toGive != null
                && isValidBid(newBid, state.initialBid, state.order.current().cards, variant)

    //    TODO combinatorial explosion in 3-player oneK.variant while generating possible actions
    override fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): Boolean {
        val allButCurrentPlayer = { (state.order - state.order.current()).map(Reviewer::player) }

        return state.toGive == null
                && state.talon is Choice.Taken
                && toGive.values.toSet().size == toGive.size
                && toGive.keys.containsAll(allButCurrentPlayer())
                && state.order.current().cards.containsAll(toGive.values)
    }

    override fun canConfirm(state: State.Review): Boolean = state.toGive != null
}