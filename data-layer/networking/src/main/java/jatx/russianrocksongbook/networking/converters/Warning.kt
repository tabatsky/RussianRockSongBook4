package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.networking.gson.WarningGson

internal fun Warning.toWarningGson() = WarningGson(
    warningType = warningType,
    artist = artist,
    title = title,
    variant = variant,
    comment = comment
)