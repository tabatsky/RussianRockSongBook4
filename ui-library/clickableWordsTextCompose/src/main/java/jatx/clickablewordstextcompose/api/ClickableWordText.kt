package jatx.clickablewordstextcompose.api

import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
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
    onWordClick: (String) -> Unit
) {
    val wordList = WordScanner(text).getWordList()

    val onClick: (link: LinkAnnotation) -> Unit = {
        val wordText = (it as? LinkAnnotation.Clickable)?.tag
        if (wordText != null) {
            var actualWord: String = wordText
            for (key in actualWordMappings.keys) {
                actualWord = actualWord.replace(key, actualWordMappings[key] ?: "")
            }
            if (actualWord in actualWordSet) {
                onWordClick(wordText)
            }
        }
    }
    val annotatedText = buildAnnotatedString {
        append(text)
        wordList.forEach {
            var actualWord = it.text
            for (key in actualWordMappings.keys) {
                actualWord = actualWord.replace(key, actualWordMappings[key] ?: "")
            }
            if (actualWord in actualWordSet) {
                addStyle(
                    SpanStyle(color = colorBg, background = colorMain, fontSize = fontSize * 1.1),
                    it.startIndex,
                    it.endIndex
                )
                addLink(
                    LinkAnnotation.Clickable(
                        tag = it.text,
                        linkInteractionListener = onClick
                    ),
                    it.startIndex,
                    it.endIndex
                )
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier
            .background(colorBg),
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize,
            color = colorMain
        )
    )
}