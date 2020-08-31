package oneK.server.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import oneK.variant.Variant
import oneK.variant.getVariantFor
import javax.inject.Singleton

@Factory
internal class VariantFactory(@Value("\${game.variant.players}") private val playersCount: String) {

    @Singleton
    fun variant(): Variant = if (playersCount.toInt() > 2) getVariantFor(3) else getVariantFor(2)
}