package oneK.game

import oneK.player.Player
import oneK.round.Round
import oneK.strategy.GameStrategy
import oneK.strategy.RoundStrategy

public val MAXIMUM_BID = 400

class Game(private val players: List<Player>,
           private val strategy: GameStrategy) {

    //initialize after bidding
    private var currentRound: Round? = null
    private var currentBid = 100
    val ranking: MutableMap<Player, Int>


    init {
        //todo check if reading from state is not overriding
        this.ranking = mutableMapOf(*(players.map { Pair(it, 0) }.toTypedArray()))
    }


    public fun startRound(strategy: RoundStrategy) {
        require(currentRound != null)

        this.currentRound = Round(players, strategy, currentBid)
    }
}