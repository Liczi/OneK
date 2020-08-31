package oneK.server.converter

import oneK.randomGame
import org.junit.jupiter.api.Test

class RandomPlayoutConverterTest {

    @Test
    fun `should properly convert generated states`() {
        randomGame { convertAndAssertEqual(it) }
    }
}