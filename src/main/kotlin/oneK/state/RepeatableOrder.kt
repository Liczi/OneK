package oneK.state

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
        val newInd = nextIndex(this.currentInd)
        return this.copy(order = this.order, currentInd = newInd)
    }

    fun replaceCurrent(currentTransform: (T) -> T): RepeatableOrder<T> {
        val newOrder = order.replaceCurrent(currentTransform(this.current()))
        return this.copy(order = newOrder)
    }

    fun replaceCurrentAndNext(currentTransform: (T) -> T): RepeatableOrder<T> {
        val newOrder = order.replaceCurrent(currentTransform(this.current()))
        val newInd = nextIndex(this.currentInd)
        return this.copy(order = newOrder, currentInd = newInd)
    }

    fun replaceCurrentAndNextUntil(newCurrent: T, condition: (T) -> Boolean): RepeatableOrder<T> {
        val newOrder = order.replaceCurrent(newCurrent)
        val newInd = nextUntil(condition)
        return this.copy(order = newOrder, currentInd = newInd)
    }

    fun previous(): RepeatableOrder<T> {
        val newInd = if (currentInd > 0) currentInd - 1 else lastInd
        return this.copy(order = this.order, currentInd = newInd)
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

    fun firstOrNull(predicate: (T) -> Boolean): T? {
        var currentIndex = this.currentInd
        for (i in this.order.indices) {
            val current = this.order[currentIndex]
            if (predicate(current))
                return current
            currentIndex = nextIndex(currentIndex)
        }
        return null
    }

    fun withCurrent(predicate: (T) -> Boolean): RepeatableOrder<T> =
        this.copy(
            order = this.order,
            currentInd = this.indexOfFirst(predicate)
        )

    private fun <R> copy(order: List<R>, currentInd: Int = this.currentInd): RepeatableOrder<R> =
        RepeatableOrder(
            order = order,
            currentInd = currentInd
        )

    private fun List<T>.replaceCurrent(newCurrent: T) =
        this.mapIndexed { index, elem -> if (index == currentInd) newCurrent else elem }

    private fun nextIndex(currentInd: Int): Int = if (currentInd < lastInd) currentInd + 1 else 0

    //    TODO is loop-based implementation better? - optimization
    @OptIn(ExperimentalStdlibApi::class)
    private fun nextUntil(condition: (T) -> Boolean): Int {
        return order.indices
            .scan(this.currentInd) { acc, _ -> nextIndex(acc) }
            .drop(1)
            .map { Pair(it, this.order[it]) }
            .first { condition(it.second) }
            .first
    }

    companion object {
        fun <T> of(order: List<T>): RepeatableOrder<T> {
            return RepeatableOrder(order)
        }
    }
}
