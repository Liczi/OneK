package oneK.v2.state

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
}
