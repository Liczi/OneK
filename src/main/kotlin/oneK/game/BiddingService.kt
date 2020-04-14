package oneK.game

import oneK.game.events.GameEvent
import oneK.player.Player

class PlayerService(var players: List<Player>) {
    var currentPlayer = players[1]
    var winner: Player? = null

    fun listPlayersFrom(first: Player): MutableList<Player> {
        val newOrder = mutableListOf(first)
        var index = players.indexOf(first)
        var count = 1
        while (count < players.size) {
            index++
            if (index >= players.size) index = 0
            newOrder.add(players[index])
            count++
        }
        return newOrder
    }


    fun nextPlayer() {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1

        this.currentPlayer = players.elementAt(nextIndex)
        if (!bidders[currentPlayer]!!) return nextPlayer()
        else this.eventPublisher.publish(GameEvent.PLAYER_CHANGED)
    }
}