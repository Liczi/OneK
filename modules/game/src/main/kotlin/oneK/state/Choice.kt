package oneK.state

sealed class Choice<T>(private val choice: List<T>) : List<T> by choice {

    fun take(chosenIndex: Int): Taken<T> =
        Taken(choice[chosenIndex], choice)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Choice<*>

        if (choice != other.choice) return false

        return true
    }

    override fun hashCode(): Int {
        return choice.hashCode()
    }

    class NotTaken<T>(choice: List<T>) : Choice<T>(choice)
    class Taken<T>(val value: T, choice: List<T>) : Choice<T>(choice)

    companion object {
        fun <T> of(choice: List<T>): Choice<T> =
            NotTaken(choice)
    }
}