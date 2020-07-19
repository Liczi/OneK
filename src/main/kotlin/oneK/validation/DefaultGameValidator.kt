package oneK.validation

import oneK.variant.Variant

// TODO add objects being default validators for specific variants e.g. TwoPlayerSummaryValidator - decreases object creation per each game obj creation, or use cached game instances
// TODO move this file to joint defaults declaration
class DefaultGameValidator(variant: Variant) :
    GameValidator,
    SummaryValidator by SummaryStateValidatorImpl(
        variant
    ),
    BiddingValidator by BiddingStateValidatorImpl(
        variant
    ),
    ReviewValidator by ReviewStateValidatorImpl(
        variant
    ),
    StrifeValidator by StrifeStateValidatorImpl(
        variant
    ) {
}