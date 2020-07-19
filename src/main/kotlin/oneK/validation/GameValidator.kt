package oneK.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.state.State

interface GameValidator {
    fun canStart(state: State.Summary): State.Summary?

    fun canBid(state: State.Bidding, bid: Int): State.Bidding?

    fun canPickTalon(state: State.Review, talonIndex: Int): State.Review?
    fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review?
    fun canActivateBomb(state: State.Review): State.Review?
    fun canRestart(state: State.Review): State.Review?
    fun canChangeBid(state: State.Review, newBid: Int): State.Review?
    fun canConfirm(state: State.Review): State.Review?

    fun canPlay(state: State.Strife, card: Card): State.Strife?
    fun canTriumph(state: State.Strife, card: Card): State.Strife?
}
