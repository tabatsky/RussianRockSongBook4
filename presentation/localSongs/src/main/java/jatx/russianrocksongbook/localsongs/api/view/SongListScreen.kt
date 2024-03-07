package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.localsongs.internal.view.songlist.SongListScreenImpl

@Composable
fun SongListScreen(artist: String, isBackFromSomeScreen: Boolean = false, songTitleToPass: String? = null) =
    SongListScreenImpl(artist, isBackFromSomeScreen, songTitleToPass)