package oneK.generator

import oneK.asCardSet
import oneK.deck.Color
import oneK.deck.Figure
import oneK.of
import oneK.state.Action
import oneK.state.PlayingEffect
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.validation.DefaultValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ActionGeneratorTest {


    @Nested
    inner class SummaryStateTest : TestStateHolder.Summary(ThreePlayer()) {

        private val generator = ActionGenerator(DefaultValidator(variant), variant)

        @Test
        fun `should generate start action on initial summary state`() {
            val actions = generator.generate(initialState)

            assertThat(actions)
                .hasSize(1)
            assertThat(actions[0])
                .isEqualTo(Action.Summary.Start)
        }

        @Test
        fun `should not generate start action on a finished game summary state`() {
            val actions = generator.generate(
                initialState.copy(
                    ranking = initialState.ranking + (players[0] to variant.getGameGoal())
                )
            )

            assertThat(actions)
                .isEmpty()
        }
    }

    @Nested
    inner class BiddingStateTest : TestStateHolder.Bidding(ThreePlayer()) {

        private val generator = ActionGenerator(DefaultValidator(variant), variant)

        @Test
        fun `should generate bid and fold actions on initial bidding state`() {
            val actions = generator.generate(initialState)

            assertThat(actions)
                .containsExactlyInAnyOrder(
                    Action.Bidding.Bid(110),
                    Action.Bidding.Fold
                )
        }

        @Test
        fun `should generate fold only action on bidding state with bid 120`() {
            val state = game.bid(initialState, 110).let { game.bid(it, 120) }
            val actions = generator.generate(state)

            assertThat(actions)
                .containsExactlyInAnyOrder(
                    Action.Bidding.Fold
                )
        }
    }

    @Nested
    inner class ReviewStateTest : TestStateHolder.Review(ThreePlayer(), listOf("9H", "JH", "JD")) {

        private val generator = ActionGenerator(DefaultValidator(variant), variant)

        @Test
        fun `should generate pick talon action`() {
            val actions = generator.generate(initialState)

            assertThat(actions)
                .containsExactlyInAnyOrder(
                    Action.Review.Pick(0)
                )
        }

        @Test
        fun `should generate distribute and restart actions`() {
            val actions = generator.generate(game.pickTalon(initialState, 0))

            assertThat(actions).hasSize(13)
            assertThat(actions).contains(Action.Review.Restart)
            assertThat(actions.filterIsInstance<Action.Review.Distribute>().map { it.toGive.entries.first().value })
                .containsOnlyElementsOf("9D,9C,9S,9H".asCardSet())
            assertThat(actions.filterIsInstance<Action.Review.Distribute>().map { it.toGive.entries.last().value })
                .containsOnlyElementsOf("9D,9C,9S,9H".asCardSet())
        }

        @Test
        fun `should generate change bid and confirm actions`() {
            val state = initialState
                .let { game.pickTalon(it, 0) }
                .let {
                    game.distributeCards(
                        it,
                        toGive = mapOf(
                            players[1] to (Figure.NINE of Color.HEARTS),
                            players[2] to (Figure.NINE of Color.DIAMONDS)
                        )
                    )
                }
            val actions = generator.generate(state)

            assertThat(actions).hasSize(3)
            assertThat(actions).contains(Action.Review.Confirm)
            assertThat(actions.filterIsInstance<Action.Review.Change>().map { it.newBid })
                .containsExactlyInAnyOrder(110, 120)
        }

        @Test
        fun `should generate confirm action`() {
            val state = initialState
                .let { game.pickTalon(it, 0) }
                .let {
                    game.distributeCards(
                        it,
                        toGive = mapOf(
                            players[1] to (Figure.NINE of Color.HEARTS),
                            players[2] to (Figure.NINE of Color.DIAMONDS)
                        )
                    )
                }
                .let { game.changeBid(it, 120) }
            val actions = generator.generate(state)

            assertThat(actions).containsExactly(Action.Review.Confirm)
        }
    }

    @Nested
    inner class StrifeStateTest : TestStateHolder.Strife(
        ThreePlayer(),
        listOf("AC, TD, QD, KD, 9S", "AD, TC, QC, KC, 9D", "AH, TH, QH, KH, 9H")
    ) {

        private val generator = ActionGenerator(DefaultValidator(variant), variant)

        @Test
        fun `should generate proper card actions scenario 1`() {
            val actions = generator.generate(initialState)

            assertThat(actions.filterIsInstance<Action.Strife.Play>().map { it.card })
                .containsExactlyInAnyOrder(
                    Figure.ACE of Color.CLUBS,
                    Figure.TEN of Color.DIAMONDS,
                    Figure.QUEEN of Color.DIAMONDS,
                    Figure.KING of Color.DIAMONDS,
                    Figure.NINE of Color.SPADES
                )
            assertThat(actions.filterIsInstance<Action.Strife.Triumph>().map { it.card })
                .containsExactlyInAnyOrder(Figure.QUEEN of Color.DIAMONDS)
        }

        @Test
        fun `should generate proper card actions scenario 2`() {
            val state = (game.play(initialState, Figure.ACE of Color.CLUBS) as PlayingEffect.NoTransition).state
            val actions = generator.generate(state)

            assertThat(actions.filterIsInstance<Action.Strife.Play>().map { it.card })
                .containsExactlyInAnyOrder(
                    Figure.KING of Color.CLUBS,
                    Figure.TEN of Color.CLUBS,
                    Figure.QUEEN of Color.CLUBS
                )
        }

        @Test
        fun `should generate proper card actions scenario 3`() {
            val state = (game.play(initialState, Figure.NINE of Color.SPADES) as PlayingEffect.NoTransition).state
            val actions = generator.generate(state)

            assertThat(actions.filterIsInstance<Action.Strife.Play>().map { it.card })
                .containsExactlyInAnyOrder(
                    Figure.ACE of Color.DIAMONDS,
                    Figure.TEN of Color.CLUBS,
                    Figure.QUEEN of Color.CLUBS,
                    Figure.KING of Color.CLUBS,
                    Figure.NINE of Color.DIAMONDS
                )
        }

        @Test
        fun `should generate proper card actions scenario 4`() {
            val state = initialState
                .let { (game.play(it, Figure.NINE of Color.SPADES) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, Figure.ACE of Color.DIAMONDS) as PlayingEffect.NoTransition).state }
            val actions = generator.generate(state)

            assertThat(actions.filterIsInstance<Action.Strife.Play>().map { it.card })
                .containsExactlyInAnyOrder(
                    Figure.ACE of Color.HEARTS,
                    Figure.TEN of Color.HEARTS,
                    Figure.QUEEN of Color.HEARTS,
                    Figure.KING of Color.HEARTS,
                    Figure.NINE of Color.HEARTS
                )
        }

        @Test
        fun `should generate proper card actions scenario 5`() {
            val state = initialState
                .let { game.triumph(it, Figure.QUEEN of Color.DIAMONDS) }
                .let { (game.play(it, Figure.ACE of Color.DIAMONDS) as PlayingEffect.NoTransition).state }
            val actions = generator.generate(state)

            assertThat(actions.filterIsInstance<Action.Strife.Play>().map { it.card })
                .containsExactlyInAnyOrder(
                    Figure.ACE of Color.HEARTS,
                    Figure.TEN of Color.HEARTS,
                    Figure.QUEEN of Color.HEARTS,
                    Figure.KING of Color.HEARTS,
                    Figure.NINE of Color.HEARTS
                )
        }
    }
}