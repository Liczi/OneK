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
    fun canPickTalon(state: State.Review, talonIndex: Int): State.Review?
    fun canActivateBomb(state: State.Review): State.Review?
    fun canRestart(state: State.Review): State.Review?
    fun canChangeBid(state: State.Review, newBid: Int): State.Review?
    fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review?
    fun canConfirm(state: State.Review): State.Review?
}

internal class ReviewStateValidatorImpl(private val variant: Variant) : ReviewValidator, StateValidator() {
    override fun canPickTalon(state: State.Review, talonIndex: Int): State.Review? {
        return state.ensureValid {
            state.talon is Choice.NotTaken
                    && talonIndex < state.talon.size
                    && talonIndex >= 0
        }
    }

    override fun canActivateBomb(state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canRestart(state: State.Review): State.Review? {
        return state.ensureValid {
            state.talon is Choice.Taken
                    && state.toGive == null
                    && state.order.current().cards.containsAllNines()
        }
    }

    override fun canChangeBid(state: State.Review, newBid: Int): State.Review? {
        return state.ensureValid {
            state.changedBid == null
                    && state.talon is Choice.Taken
                    && state.toGive != null
                    && isValidBid(newBid, state.initialBid, state.order.current().cards, variant)
        }
    }

    //    TODO combinatorial explosion in 3-player oneK.variant while generating possible actions
    override fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review? {
        return state.ensureValid {
            val allButCurrentPlayer = { (state.order - state.order.current()).map(Reviewer::player) }

            state.toGive == null
                    && state.talon is Choice.Taken
                    && toGive.values.toSet().size == toGive.size
                    && toGive.keys.containsAll(allButCurrentPlayer())
                    && state.order.current().cards.containsAll(toGive.values)
        }
    }

    override fun canConfirm(state: State.Review): State.Review? {
        return state.ensureValid {
            state.toGive != null
        }
    }
}