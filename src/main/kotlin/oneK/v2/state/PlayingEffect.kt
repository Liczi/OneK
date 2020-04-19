package oneK.v2.state

sealed class PlayingEffect {
    class SummaryTransition(val state: State.Summary) : PlayingEffect()
    object NoTransition : PlayingEffect()
}