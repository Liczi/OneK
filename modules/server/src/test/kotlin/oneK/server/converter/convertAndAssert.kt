package oneK.server.converter

import oneK.state.State
import org.assertj.core.api.Assertions

fun convertAndAssertEqual(state: State) {
    val converted = state.toProtoMessage().toModel()
    Assertions.assertThat(state).isEqualToComparingFieldByField(converted)
}