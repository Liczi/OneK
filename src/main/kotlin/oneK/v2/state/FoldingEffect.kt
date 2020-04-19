package oneK.v2.state

sealed class FoldingEffect {
    class ReviewTransition(val state: State.Review) : FoldingEffect()
    object NoTransition : FoldingEffect()
}