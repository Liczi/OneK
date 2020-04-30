package oneK.v2.validation

import oneK.v2.state.State

interface ReviewValidator {
    fun canActivateBomb(state: State.Review): Boolean
    fun canRestart(state: State.Review): Boolean
    fun canChangeBid(state: State.Review, newBid: Int): Boolean
    fun canConfirmBid(state: State.Review): Boolean
}