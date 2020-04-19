package oneK.round

import oneK.player.Player
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventPublisher

class SummaryService(
        private val players: List<Player>,
        private val biddingPlayer: Player,
        private val bid: Int,
        private val eventPublisher: RoundEventPublisher
) {
    var score: MutableMap<Player, Int> = players.associateWithTo(mutableMapOf(), { 0 })
    /**
     * Bidding player is confronted against his bid and score table is rounded to decimal
     */
    fun endRound() {
        require(this.score.keys.contains(biddingPlayer))
        var bidderScore = this.score[biddingPlayer]!!
        bidderScore = if (bidderScore >= bid) bid else -bid

        this.score[biddingPlayer] = bidderScore

//        this.score.replace(biddingPlayer, bidderScore)
        this.score.round()
        this.eventPublisher.publish(RoundEvent.ROUND_ENDED)
    }


    fun addPoints(player: Player, points: Int) {
        require(this.score.keys.contains(player))
        val previousScore = this.score[player]!!
        this.score[player] = previousScore + points

//        this.score.replace(player, previousScore + points)
    }

    fun getScoreValues() = this.players.map { this.score[it]!! }.toIntArray()

    fun getCurrentScore() = this.score

}