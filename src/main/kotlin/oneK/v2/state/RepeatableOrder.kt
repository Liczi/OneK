package oneK.v2.state

class RepeatableOrder<T> private constructor(private val order: List<T>, private val currentInd: Int = 0) :
    List<T> by order {

    private val lastInd = order.size - 1

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

    fun next(): RepeatableOrder<T> {
        val currentInd = nextIndex()
        return this.copy(order = this.order, currentInd = currentInd)
    }

    fun replaceCurrent(newCurrent: T): RepeatableOrder<T> {
        val newOrder = order.replaceCurrent(newCurrent)
        return this.copy(order = newOrder)
    }

    fun replaceCurrentAndNext(newCurrent: T): RepeatableOrder<T> {
        val newOrder = order.replaceCurrent(newCurrent)
        val currentInd = nextIndex()
        return this.copy(order = newOrder, currentInd = currentInd)
    }

    fun previous(): RepeatableOrder<T> {
        val currentInd = if (currentInd > 0) currentInd - 1 else lastInd
        return this.copy(order = this.order, currentInd = currentInd)
    }

    fun current(): T = order[currentInd]

    fun <R> map(transform: (T) -> R): RepeatableOrder<R> =
        this.copy(
            order = this.order.map(transform),
            currentInd = this.currentInd
        )

    fun mapNotCurrent(transform: (T) -> T): RepeatableOrder<T> =
        this.copy(
            order = this.order.mapIndexed { index, elem -> if (index != currentInd) transform(elem) else elem },
            currentInd = this.currentInd
        )

    fun <R> zip(iterable: Iterable<R>): RepeatableOrder<Pair<T, R>> =
        this.copy(
            order = this.order.zip(iterable),
            currentInd = this.currentInd
        )

    private fun <R> copy(order: List<R>, currentInd: Int = this.currentInd): RepeatableOrder<R> =
        RepeatableOrder(
            order = order,
            currentInd = currentInd
        )

    private fun List<T>.replaceCurrent(newCurrent: T) =
        this.mapIndexed { index, elem -> if (index == currentInd) newCurrent else elem }

    private fun nextIndex(): Int = if (this.currentInd < lastInd) this.currentInd + 1 else 0

    companion object {
        fun <T> of(order: List<T>): RepeatableOrder<T> {
            return RepeatableOrder(order)
        }
    }
}
