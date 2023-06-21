package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.localsongs.internal.view.songlist.SongListScreenImpl

@Composable
fun SongListScreen(artist: String, isBackFromSong: Boolean = false, passToSongWithTitle: String? = null) =
    SongListScreenImpl(artist, isBackFromSong, passToSongWithTitle)