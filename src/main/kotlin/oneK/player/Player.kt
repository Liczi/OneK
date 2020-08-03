package oneK.player

import java.io.Serializable
import java.util.*

class Player(val name: String) : Serializable {
    private val uuid = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (other !is Player) return false
        if (other === this) return true

        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "Player(name='$name')"
    }
}