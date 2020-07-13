package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.State
import oneK.v2.variant.Variant

// TODO add objects being default validators for specific variants e.g. TwoPlayerSummaryValidator - decreases object creation per each game obj creation, or use cached game instances
// TODO move this file to joint defaults declaration
class DefaultGameValidator(variant: Variant) :
    GameValidator,
    SummaryValidator by SummaryValidatorImpl(variant),
    BiddingValidator by BiddingStateValidatorImpl(variant),
    ReviewValidator by ReviewStateValidatorImpl(variant) {

    //    TODO make all methods be implemented by delegates
    override fun canPlay(state: State.Strife, card: Card): State.Strife? {
        TODO("Not yet implemented")
    }

    override fun canTriumph(state: State.Strife, card: Card): State.Strife? {
        TODO("Not yet implemented")
    }
}