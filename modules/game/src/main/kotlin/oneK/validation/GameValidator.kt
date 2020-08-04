package oneK.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.state.State
import oneK.variant.Variant

interface GameValidator {
    fun canStart(state: State.Summary): Boolean

    fun canBid(state: State.Bidding, bid: Int): Boolean

    fun canPickTalon(state: State.Review, talonIndex: Int): Boolean
    fun canDistributeCards(state: State.Review, toGive: Map<Player, Card>): Boolean
    fun canActivateBomb(state: State.Review): Boolean
    fun canRestart(state: State.Review): Boolean
    fun canChangeBid(state: State.Review, newBid: Int): Boolean
    fun canConfirm(state: State.Review): Boolean

    fun canPlay(state: State.Strife, card: Card): Boolean
    fun canTriumph(state: State.Strife, card: Card): Boolean
}

class DefaultGameValidator(variant: Variant) :
    GameValidator,
    SummaryValidator by SummaryStateValidatorImpl(variant),
    BiddingValidator by BiddingStateValidatorImpl(variant),
    ReviewValidator by ReviewStateValidatorImpl(variant),
    StrifeValidator by StrifeStateValidatorImpl(variant)