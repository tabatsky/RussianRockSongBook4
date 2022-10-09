package jatx.clickablewordstextview.api

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import jatx.clickablewordstextview.internal.WordScanner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class ClickableWordsTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    AppCompatTextView(context, attrs, defStyleAttr) {
    private var txt: CharSequence? = null
    private var onWordClick: (Word) -> Unit = {}

    var actualWordSet = setOf<String>()
    var actualWordMappings = hashMapOf<String, String>()
    val wordFlow: Flow<Word> = callbackFlow {
        onWordClick =  {
            trySend(it)
        }
        awaitClose {
            onWordClick = {}
        }
    }.buffer(Channel.CONFLATED)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    override fun setText(text: CharSequence, type: BufferType) {
        txt = text
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
        val spannableString = SpannableString(text)
        val wordList = WordScanner(txt.toString()).getWordList()
        wordList.forEach { word: Word ->
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    var actualWord = word.text
                    for (key in actualWordMappings.keys) {
                        actualWord = actualWord.replace(key, actualWordMappings[key] ?: "")
                    }
                    if (actualWord in actualWordSet) {
                        onWordClick(word)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    var actualWord = word.text
                    for (key in actualWordMappings.keys) {
                        actualWord = actualWord.replace(key, actualWordMappings[key] ?: "")
                    }
                    if (actualWord in actualWordSet) {
                        ds.color = (background as ColorDrawable).color
                        ds.bgColor = currentTextColor
                    } else {
                        ds.color = currentTextColor
                        ds.bgColor = (background as ColorDrawable).color
                    }
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(clickableSpan, word.startIndex, word.endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        super.setText(spannableString, type)
    }
}
