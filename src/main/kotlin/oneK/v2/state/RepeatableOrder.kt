package oneK.v2.state

//TODO add predicate!!!
class RepeatableOrder<T>(private val order: List<T>): List<T> by order {
    private val lastInd = order.size - 1
    private var currentInd = 0

    fun next() {
        this.currentInd = if (currentInd < lastInd) currentInd + 1 else 0
    }

    fun previous() {
        this.currentInd = if (currentInd > 0) currentInd - 1 else lastInd
    }

    fun current(): T = order[currentInd]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RepeatableOrder<*>

        if (order != other.order) return false
        if (currentInd != other.currentInd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = order.hashCode()
        result = 31 * result + currentInd
        return result
    }


}
