package jatx.russianrocksongbook.commonview.ext

fun String.crop(maxLength: Int) =
    if (this.length <= maxLength)
        this
    else
        this.take(maxLength - 1) + "…"