package jatx.russianrocksongbook.textsearch.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.textsearch.internal.view.textsearchlist.TextSearchListScreenImpl

@Composable
fun TextSearchListScreen(randomKey: Int, isBackFromSong: Boolean) =
    TextSearchListScreenImpl(randomKey, isBackFromSong)