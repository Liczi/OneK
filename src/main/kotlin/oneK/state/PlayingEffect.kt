package oneK.state

sealed class PlayingEffect {
    class SummaryTransition(val state: State.Summary) : PlayingEffect()
    class NoTransition(val state: State.Strife) : PlayingEffect()
}