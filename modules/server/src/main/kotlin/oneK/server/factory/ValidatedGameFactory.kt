package oneK.server.factory

import io.micronaut.context.annotation.Factory
import oneK.GameFactory
import oneK.ValidatedGame
import oneK.variant.Variant
import javax.inject.Singleton

@Factory
internal class ValidatedGameFactory(private val variant: Variant) {

    @Singleton
    fun game(): ValidatedGame = GameFactory.default(variant)
}