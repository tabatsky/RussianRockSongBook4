package jatx.clickablewordstextcompose.api

import androidx.compose.foundation.background
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import jatx.clickablewordstextcompose.internal.WordScanner

@Composable
fun ClickableWordText(
    text: String,
    actualWordSet: Set<String>,
    actualWordMappings: Map<String, String>,
    modifier: Modifier,
    colorMain: Color,
    colorBg: Color,
    fontSize: TextUnit,
    onWordClick: (Word) -> Unit
) {
    val wordList = WordScanner(text).getWordList()

    val annotatedText = AnnotatedString(
        text = text,
        spanStyles = wordList.mapNotNull {
            var actualWord = it.text
            for (key in actualWordMappings.keys) {
                actualWord = actualWord.replace(key, actualWordMappings[key] ?: "")
            }
            if (actualWord in actualWordSet) {
                AnnotatedString.Range(
                    SpanStyle(color = colorBg, background = colorMain, fontSize = fontSize * 1.1),
                    it.startIndex,
                    it.endIndex
                )
            } else {
                null
            }
        }
    )

    ClickableText(
        text = annotatedText,
        modifier = modifier
            .background(colorBg),
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize,
            color = colorMain
        )
    ) { offset ->
        val word = wordList.firstOrNull { offset >= it.startIndex && offset <= it.endIndex }
        word?.let {
            var actualWord = word.text
            for (key in actualWordMappings.keys) {
                actualWord = actualWord.replace(key, actualWordMappings[key] ?: "")
            }
            if (actualWord in actualWordSet) {
                onWordClick(word)
            }
        }
    }
}