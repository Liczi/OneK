package oneK.v2

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.state.*
import oneK.v2.validation.GameValidator

abstract class ValidatedGame(
    private val validator: GameValidator
) {
    protected abstract fun doStart(state: State.Summary): State.Bidding

    protected abstract fun doBid(state: State.Bidding, bid: Int): State.Bidding
    protected abstract fun doFold(state: State.Bidding): FoldingEffect

    protected abstract fun doPickTalon(state: State.Review, talonIndex: Int): State.Review
    protected abstract fun doDistributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review
    protected abstract fun doActivateBomb(state: State.Review): State.Summary
    protected abstract fun doRestart(state: State.Review): State.Bidding
    protected abstract fun doChangeBid(state: State.Review, newBid: Int): State.Review
    protected abstract fun doConfirm(state: State.Review): State.Strife

    protected abstract fun doPlay(state: State.Strife, card: Card): PlayingEffect
    protected abstract fun doTriumph(state: State.Strife, card: Card): State.Strife

    //    TODO validate state and call can* function if exists then call do* - add proper abstraction for that
    fun start(state: State.Summary): State.Bidding =
        validator.canStart(state)
            ?.let { doStart(it) }
            ?: illegalState(state)

    fun bid(state: State.Bidding, bid: Int): State.Bidding =
        validator.canBid(state, bid)
            ?.let { doBid(it, bid) }
            ?: illegalState(state)

    fun fold(state: State.Bidding): FoldingEffect = doFold(state)

    fun pickTalon(state: State.Review, talonIndex: Int): State.Review =
        validator.canPickTalon(state, talonIndex)
            ?.let { doPickTalon(it, talonIndex) }
            ?: illegalState(state)

    fun distributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review =
        validator.canDistributeCards(state, toGive)
            ?.let { doDistributeCards(it, toGive) }
            ?: illegalState(state)

    fun activateBomb(state: State.Review): State.Summary =
        validator.canActivateBomb(state)
            ?.let { doActivateBomb(it) }
            ?: illegalState(state)


    fun restart(state: State.Review): State.Bidding =
        validator.canRestart(state)
            ?.let { doRestart(it) }
            ?: illegalState(state)

    fun changeBid(state: State.Review, newBid: Int): State.Review =
        validator.canChangeBid(state, newBid)
            ?.let { doChangeBid(it, newBid) }
            ?: illegalState(state)

    fun confirm(state: State.Review): State.Strife =
        validator.canConfirm(state)
            ?.let { doConfirm(it) }
            ?: illegalState(state)

    fun play(state: State.Strife, card: Card): PlayingEffect =
        validator.canPlay(state, card)
            ?.let { doPlay(it, card) }
            ?: illegalState(state)

    fun triumph(state: State.Strife, card: Card): State.Strife =
        validator.canTriumph(state, card)
            ?.let { doTriumph(it, card) }
            ?: illegalState(state)

    private fun <T> illegalState(state: State): T {
        throw IllegalStateException("Illegal action on state: $state")
    }
}
