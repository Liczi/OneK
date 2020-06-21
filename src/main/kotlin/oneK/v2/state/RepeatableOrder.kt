package oneK.v2.state

//TODO add predicate!!!
class RepeatableOrder<T> private constructor(private val order: List<T>, private val currentInd: Int = 0) :
    List<T> by order {
    private val lastInd = order.size - 1

    fun next(): RepeatableOrder<T> {
        val currentInd = if (this.currentInd < lastInd) this.currentInd + 1 else 0
        return RepeatableOrder(order, currentInd)
    }

    //    TODO refactor
    fun next(newCurrent: T): RepeatableOrder<T> {
        val newOrder = order.mapIndexed { index, elem -> if (index == currentInd) newCurrent else elem }
        val currentInd = if (this.currentInd < lastInd) this.currentInd + 1 else 0
        return RepeatableOrder(newOrder, currentInd)
    }

    fun previous(): RepeatableOrder<T> {
        val currentInd = if (currentInd > 0) currentInd - 1 else lastInd
        return RepeatableOrder(order, currentInd)
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

    override fun toString(): String {
        return "RepeatableOrder(order=$order, currentInd=$currentInd)"
    }

    companion object {
        fun <T> of(order: List<T>): RepeatableOrder<T> {
            return RepeatableOrder(order)
        }
    }
}
