package oneK.v2.validation

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.*
import oneK.v2.variant.Variant

interface GameValidator {
    //    TODO change to extension functions on gameState
//    add singleton DefaultGameValidator
//    fun GameState.canStart(gameState: GameState): State.BiddingState
    fun canBid(bid: Int, gameState: GameState, variant: Variant): Boolean
    fun canFold(gameState: GameState, player: Player): Boolean
    fun canActivateBomb(gameState: GameState): Boolean
    fun canRestart(state: State.Bidding): Boolean
    fun canChangeBid(gameState: GameState, newBid: Int): Boolean
    fun canConfirmBid(gameState: GameState): Boolean
    fun canPlay(gameState: GameState, card: Card): Boolean
    fun canTriumph(gameState: GameState, card: Card): Boolean
}
