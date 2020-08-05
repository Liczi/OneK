package oneK.state

sealed class Choice<T>(private val choice: List<T>) : List<T> by choice {

    fun take(chosenIndex: Int): Taken<T> =
        Taken(choice[chosenIndex], choice)

    class NotTaken<T>(choice: List<T>) : Choice<T>(choice)
    class Taken<T>(val value: T, choice: List<T>) : Choice<T>(choice)

    companion object {
        fun <T> of(choice: List<T>): Choice<T> =
            NotTaken(choice)
    }
}