package oneK.state

sealed class FoldingEffect {
    fun state(): State =
        when (this) {
            is ReviewTransition -> this.state
            is NoTransition -> this.state
        }

    class ReviewTransition(val state: State.Review) : FoldingEffect()
    class NoTransition(val state: State.Bidding) : FoldingEffect()
}