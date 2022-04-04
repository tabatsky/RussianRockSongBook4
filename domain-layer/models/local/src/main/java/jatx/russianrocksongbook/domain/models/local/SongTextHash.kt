package jatx.russianrocksongbook.domain.models.local

import jatx.russianrocksongbook.util.hashing.HashingUtil

fun songTextHash(text: String): String {
    val preparedText =
        text.trim { it <= ' ' }.lowercase().replace("\\s+".toRegex(), " ")
    return HashingUtil.md5(preparedText)
}