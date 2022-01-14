package jatx.clickablewordstextview.internal

import jatx.clickablewordstextview.api.Word

internal interface OnWordClickListener {
    fun onWordClick(word: Word)
}

internal fun onWordClickListener(onWordClickLambda: (Word) -> Unit) = object :
    OnWordClickListener {
    override fun onWordClick(word: Word) {
        onWordClickLambda(word)
    }
}