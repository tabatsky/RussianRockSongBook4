package jatx.russianrocksongbook.textsearch.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.textsearch.internal.view.textsearchsongtext.TextSearchSongTextScreenImpl

@Composable
fun TextSearchSongTextScreen(position: Int) =
    TextSearchSongTextScreenImpl(position)