package oneK.service

import oneK.deck.Card
import oneK.state.Action
import oneK.state.State

interface StrifeService {
    fun State.Strife.performPlay(card: Card): State.Strife
    fun State.Strife.performTriumph(card: Card): State.Strife
}

internal object DefaultStrifeServiceImpl : StrifeService {

    override fun State.Strife.performPlay(card: Card): State.Strife {
        return this.copy(
            order = this.order.replaceCurrentAndNext {
                it.copy(
                    cards = it.cards - card,
                    lastAction = Action.Strife.Play(card)
                )
            }
        )
    }

    override fun State.Strife.performTriumph(card: Card): State.Strife {
        return this.copy(
            order = this.order.replaceCurrentAndNext {
                it.copy(
                    cards = it.cards - card,
                    lastAction = Action.Strife.Triumph(card),
                    points = it.points + card.color.value
                )
            },
            currentTriumph = card.color
        )
    }
}
