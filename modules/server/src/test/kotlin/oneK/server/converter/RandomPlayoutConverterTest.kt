package oneK.server.converter

import oneK.randomPlayout
import org.junit.jupiter.api.Test

class RandomPlayoutConverterTest {

    @Test
    fun `should properly convert generated states`() {
        randomPlayout { convertAndAssertEqual(it) }
    }
}