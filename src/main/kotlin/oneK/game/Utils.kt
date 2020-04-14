package oneK.game

import oneK.player.Player

fun listPlayersFrom(first: Player, players: List<Player>): MutableList<Player> {
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