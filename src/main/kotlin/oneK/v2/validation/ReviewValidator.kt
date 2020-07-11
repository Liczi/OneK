package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.containsAllNines
import oneK.v2.state.Choice
import oneK.v2.state.State
import oneK.v2.variant.Variant

//TODO add tests
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
        return state.ensureValid { false } //todo for now disallow bombing
    }

    override fun canRestart(state: State.Review): State.Review? {
        return state.ensureValid {
            state.playersOrder.current().cards.containsAllNines()
        }
    }

    override fun canChangeBid(state: State.Review, newBid: Int): State.Review? {
        return state.ensureValid {
            state.changedBid == null
                    && isValidBid(newBid, state.initialBid, state.playersOrder.current().cards, variant)
        }
    }

    //    TODO combinatorial explosion in 3-player variant
    override fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review? {
        return state.ensureValid {
            state.toGive == null
                    && toGive.size == state.playersOrder.size - 1
                    && toGive.values.toSet().size == toGive.size
                    && state.playersOrder.current().cards.containsAll(toGive.values)
        }
    }

    override fun canConfirm(state: State.Review): State.Review? {
        return state.ensureValid {
            state.toGive != null
        }
    }
}