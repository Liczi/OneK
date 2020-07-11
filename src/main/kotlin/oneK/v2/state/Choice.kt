package oneK.v2.state

sealed class Choice<T>(private val choice: List<T>) : List<T> by choice {

    fun take(chosenIndex: Int): Choice<T> {
        return if (chosenIndex in choice.indices)
            Taken(choice[chosenIndex], choice)
        else {
            NotTaken(choice)
        }
    }

    class NotTaken<T>(choice: List<T>) : Choice<T>(choice)
    class Taken<T>(val value: T, choice: List<T>) : Choice<T>(choice)

    companion object {
        fun <T> of(choice: List<T>): Choice<T> = NotTaken(choice)
    }
}