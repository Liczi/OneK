package oneK

import oneK.deck.Card
import oneK.player.Player
import oneK.state.Action
import oneK.state.FoldingEffect
import oneK.state.PlayingEffect
import oneK.state.State
import oneK.validation.Validator

abstract class ValidatedGame(
    val validator: Validator
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

    fun start(state: State.Summary): State.Bidding =
        if (validator.canStart(state))
            doStart(state)
        else illegalState(state)

    fun bid(state: State.Bidding, bid: Int): State.Bidding =
        if (validator.canBid(state, bid))
            doBid(state, bid)
        else illegalState(state, "bid" to bid)

    fun fold(state: State.Bidding): FoldingEffect = doFold(state)

    fun pickTalon(state: State.Review, talonIndex: Int): State.Review =
        if (validator.canPickTalon(state, talonIndex))
            doPickTalon(state, talonIndex)
        else illegalState(state, "talonIndex" to talonIndex)

    fun distributeCards(state: State.Review, toGive: Map<Player, Card>): State.Review =
        if (validator.canDistributeCards(state, toGive))
            doDistributeCards(state, toGive)
        else illegalState(state, toGive)

    fun activateBomb(state: State.Review): State.Summary =
        if (validator.canActivateBomb(state))
            doActivateBomb(state)
        else illegalState(state)


    fun restart(state: State.Review): State.Bidding =
        if (validator.canRestart(state))
            doRestart(state)
        else illegalState(state)

    fun changeBid(state: State.Review, newBid: Int): State.Review =
        if (validator.canChangeBid(state, newBid))
            doChangeBid(state, newBid)
        else illegalState(state, "newBid" to newBid)

    fun confirm(state: State.Review): State.Strife =
        if (validator.canConfirm(state))
            doConfirm(state)
        else illegalState(state)

    fun play(state: State.Strife, card: Card): PlayingEffect =
        if (validator.canPlay(state, card))
            doPlay(state, card)
        else illegalState(state, "card" to card)

    fun triumph(state: State.Strife, card: Card): State.Strife =
        if (validator.canTriumph(state, card))
            doTriumph(state, card)
        else illegalState(state, "card" to card)

    fun perform(action: Action, state: State): State =
        when (action) {
            Action.Summary.Start -> this.start(state as State.Summary)
            is Action.Bidding.Bid -> this.bid(state as State.Bidding, action.amount)
            Action.Bidding.Fold -> this.fold(state as State.Bidding).state()
            is Action.Review.Pick -> this.pickTalon(state as State.Review, action.talonInd)
            is Action.Review.Change -> this.changeBid(state as State.Review, action.newBid)
            is Action.Review.Distribute -> this.distributeCards(state as State.Review, action.toGive)
            Action.Review.Restart -> this.restart(state as State.Review)
            Action.Review.Confirm -> this.confirm(state as State.Review)
            is Action.Strife.Play -> this.play(state as State.Strife, action.card).state()
            is Action.Strife.Triumph -> this.triumph(state as State.Strife, action.card)
        }

    private fun <T> illegalState(state: State, vararg arguments: Any): T {
        throw IllegalStateException("Illegal action on oneK.state: $state, arguments: ${arguments.joinToString()}")
    }

}
