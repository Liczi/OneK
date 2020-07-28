package oneK.state

sealed class FoldingEffect {
    class ReviewTransition(val state: State.Review) : FoldingEffect()
    class NoTransition(val state: State.Bidding) : FoldingEffect()
}