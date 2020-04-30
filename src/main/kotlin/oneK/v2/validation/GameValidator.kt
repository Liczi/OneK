package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.*

interface GameValidator {
    // TODO change if to when statements with error message
    fun canStart(state: State.Summary): State.Bidding?

    fun canBid(bid: Int, state: State.Bidding): State.Bidding?

    fun canPickTalon(state: State.Review): State.Review?
    fun canDistributeCards(toGive: Map<Player, Card>, state: State.Review): State.Review?
    fun canActivateBomb(state: State.Review): State.Review?
    fun canRestart(state: State.Review): State.Review?
    fun canChangeBid(newBid: Int, state: State.Review): State.Review?
    fun canConfirmBid(state: State.Review): State.Review?

    fun canPlay(card: Card, state: State.Strife): State.Strife?
    fun canTriumph(card: Card, state: State.Strife): State.Strife?
}
