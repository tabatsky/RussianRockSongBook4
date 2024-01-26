package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.localsongs.internal.view.songlist.SongListScreenImpl

@Composable
fun SongListScreen(artist: String, isBackFromSomeScreen: Boolean = false, passToSongWithTitle: String? = null) =
    SongListScreenImpl(artist, isBackFromSomeScreen, passToSongWithTitle)