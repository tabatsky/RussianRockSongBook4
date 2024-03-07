package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.networking.apimodels.WarningApiModel

internal fun Warning.toWarningApiModel() = WarningApiModel(
    warningType = warningType,
    artist = artist,
    title = title,
    variant = variant,
    comment = comment
)