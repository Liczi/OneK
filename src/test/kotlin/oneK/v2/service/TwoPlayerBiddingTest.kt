package oneK.v2.service

import oneK.v2.Hand
import oneK.v2.state.Bidder
import oneK.v2.state.RepeatableOrder
import oneK.v2.state.State
import oneK.v2.toCardSet

abstract class TwoPlayerBiddingTest : TwoPlayerTest() {
    protected val initialBiddingState = State.Bidding(
        RepeatableOrder(
            listOf(
                Bidder(Hand("JS,JC".toCardSet(), players[1])),
                Bidder(Hand("JD,JH".toCardSet(), players[0]))
            )
        ),
        listOf("9S,9C".toCardSet(), "9D,9H".toCardSet())
    )
}