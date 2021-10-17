package jatx.clickablewordstextview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.dqt.libs.chorddroid.classes.ChordLibrary

class ClickableWordsTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    AppCompatTextView(context, attrs, defStyleAttr) {
    private var txt: CharSequence? = null
    var onWordClickListener: OnWordClickListener? = null

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
                    //Toast.makeText(widget.context, word.toString(), Toast.LENGTH_LONG).show()
                    onWordClickListener?.onWordClick(word)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    val actualChord = word
                        .text
                        .replace("H", "A")
                        .replace("D#", "Eb")
                        .replace("A#", "Bb")
                    if (actualChord in ChordLibrary.baseChords.keys) {
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
