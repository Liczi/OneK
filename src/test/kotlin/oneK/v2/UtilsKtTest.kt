package oneK.v2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UtilsKtTest {

    @Test
    fun `should not use the same list instance`() {
        val list = listOf(1, 2, 3)

        assertTrue(list.rotate() !== list)
    }

    @Test
    fun `should correctly rotate 3 element list`() {
        val list = listOf(1, 2, 3)

        assertThat(list.rotate()).containsExactly(2, 3, 1)
    }

    @Test
    fun `should correctly rotate 2 element list`() {
        val list = listOf(1, 2)

        assertThat(list.rotate()).containsExactly(2, 1)
    }
}