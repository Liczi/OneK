package oneK.state

sealed class PlayingEffect {
    fun state(): State =
        when (this) {
            is SummaryTransition -> this.state
            is NoTransition -> this.state
        }


    class SummaryTransition(val state: State.Summary) : PlayingEffect()
    class NoTransition(val state: State.Strife) : PlayingEffect()
}