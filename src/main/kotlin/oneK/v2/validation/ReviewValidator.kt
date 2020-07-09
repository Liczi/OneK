package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.containsAllNines
import oneK.v2.state.State
import oneK.v2.variant.Variant

//TODO add tests
interface ReviewValidator {
    fun canActivateBomb(state: State.Review): State.Review? //todo for now disallow bombing
    fun canRestart(state: State.Review): State.Review?
    fun canChangeBid(state: State.Review, newBid: Int): State.Review?
    fun canDistributeCards(state: State.Review, toGive: Pair<Player, Card>): State.Review?
    fun canConfirm(state: State.Review): State.Review?
}

internal class ReviewStateValidatorImpl(private val variant: Variant) : ReviewValidator, StateValidator() {
    override fun canActivateBomb(state: State.Review): State.Review? {
        return state.ensureValid { false }
    }

    override fun canRestart(state: State.Review): State.Review? {
        return state.ensureValid {
            state.playersOrder.current().cards.containsAllNines()
        }
    }

    override fun canChangeBid(state: State.Review, newBid: Int): State.Review? {
        return state.ensureValid {
            state.changedBid == null &&
                    isValidBid(newBid, state.initialBid, state.playersOrder.current().cards, variant)
        }
    }

    override fun canDistributeCards(state: State.Review, toGive: Pair<Player, Card>): State.Review? {
        TODO("Check if toGive is complete and state.toGive is empty")
    }

    override fun canConfirm(state: State.Review): State.Review? {
        TODO("Check if previous, mandatory actions have been executed")
    }
}