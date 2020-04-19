package oneK.v2.state

//TODO can states be vals ?
data class GameState(
        var biddingState: State.Bidding,
        var reviewState: State.Review,
        var strifeState: State.Strife,
        var summaryState: State.Summary,
        var currentState: State = biddingState
)
