package oneK.round

import oneK.deck.Card
import oneK.deck.Hand
import oneK.player.Player

//TODO merge utils or assign meaningful names

//EXTENSION


fun MutableMap<Player, Int>.round() {

    val itr = this.keys.iterator()
    while (itr.hasNext()) {
        val key = itr.next()
        val value = this[key]!!.toDouble()
        this.set(key, kotlin.math.round(value).toInt())
    }
//        this.keys.forEach { player ->
//            val currentScore = this[player]!!.toDouble()
//            this.replace(player, round(currentScore).toInt())
//        }
}