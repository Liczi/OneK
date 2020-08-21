package oneK.server

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import oneK.GameFactory
import oneK.ValidatedGame
import oneK.variant.getVariantFor
import javax.inject.Singleton

@Factory
internal class ValidatedGameFactory(@Value("\${game.variant.players}") private val playersCount: String) {

    @Singleton
    fun game(): ValidatedGame {
        val variant = if (playersCount.toInt() > 2) getVariantFor(3) else getVariantFor(2)
        return GameFactory.default(variant)
    }
}