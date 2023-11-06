package jatx.russianrocksongbook.domain.models.warning

interface Warnable {
    fun warningWithComment(comment: String): Warning
}