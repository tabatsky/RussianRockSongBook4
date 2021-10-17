package jatx.clickablewordstextview

interface OnWordClickListener {
    fun onWordClick(word: Word)
}

fun onWordClickListener(onWordClickLambda: (Word) -> Unit) = object :
    OnWordClickListener {
    override fun onWordClick(word: Word) {
        onWordClickLambda(word)
    }
}