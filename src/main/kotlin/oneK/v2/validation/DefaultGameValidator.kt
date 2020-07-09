package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.State
import oneK.v2.variant.Variant

//TODO avoid creating validator objects as they should be stateless
class DefaultGameValidator(variant: Variant) :
    GameValidator,
    SummaryValidator by SummaryValidatorImpl(variant),
    BiddingValidator by BiddingStateValidatorImpl(variant) {

//    TODO make all methods be implemented by delegates

    override fun canPickTalon(state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canDistributeCards(toGive: Map<Player, Card>, state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canActivateBomb(state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canRestart(state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canChangeBid(newBid: Int, state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canConfirmBid(state: State.Review): State.Review? {
        TODO("Not yet implemented")
    }

    override fun canPlay(card: Card, state: State.Strife): State.Strife? {
        TODO("Not yet implemented")
    }

    override fun canTriumph(card: Card, state: State.Strife): State.Strife? {
        TODO("Not yet implemented")
    }

}