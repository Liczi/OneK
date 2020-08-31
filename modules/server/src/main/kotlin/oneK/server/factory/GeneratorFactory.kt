package oneK.server.factory

import io.micronaut.context.annotation.Factory
import oneK.ValidatedGame
import oneK.generator.ActionGenerator
import oneK.variant.Variant
import javax.inject.Singleton

@Factory
internal class GeneratorFactory(private val game: ValidatedGame, private val variant: Variant) {

    @Singleton
    fun generator(): ActionGenerator = ActionGenerator(game.validator, variant)
}