package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.GameState
import oneK.v2.state.State

object DefaultGameValidator : GameValidator, BiddingValidator by BiddingValidatorImpl {

    override fun canFold(gameState: GameState, player: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun canActivateBomb(gameState: GameState): Boolean {
        TODO("Not yet implemented")
    }

    override fun canRestart(state: State.Bidding): Boolean{
        TODO("Not yet implemented")
    }

    override fun canChangeBid(gameState: GameState, newBid: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun canConfirmBid(gameState: GameState): Boolean {
        TODO("Not yet implemented")
    }

    override fun canPlay(gameState: GameState, card: Card): Boolean {
        TODO("Not yet implemented")
    }

    override fun canTriumph(gameState: GameState, card: Card):Boolean {
        TODO("Not yet implemented")
    }
}