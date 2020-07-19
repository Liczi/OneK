package oneK.service

import oneK.deck.Card
import oneK.state.State
import oneK.state.StrifeAction

interface StrifeService {
    fun State.Strife.performPlay(card: Card): State.Strife
    fun State.Strife.performTriumph(card: Card): State.Strife
}

internal object DefaultStrifeServiceImpl : StrifeService {

    override fun State.Strife.performPlay(card: Card): State.Strife {
        val current = this.order.current()
        return this.copy(
            order = this.order.replaceCurrentAndNext(
                current.copy(
                    cards = current.cards - card,
                    lastAction = StrifeAction.Play(card)
                )
            )
        )
    }

    override fun State.Strife.performTriumph(card: Card): State.Strife {
        val current = this.order.current()
        return this.copy(
            order = this.order.replaceCurrentAndNext(
                current.copy(
                    cards = current.cards - card,
                    lastAction = StrifeAction.Triumph(card),
                    points = current.points + card.color.value
                )
            ),
            currentTriumph = card.color
        )
    }
}
