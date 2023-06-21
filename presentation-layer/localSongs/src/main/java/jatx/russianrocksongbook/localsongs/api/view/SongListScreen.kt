package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.localsongs.internal.view.songlist.SongListScreenImpl

@Composable
fun SongListScreen(artist: String, isBackFromSong: Boolean, onSuccess: (() -> Unit)? = null) =
    SongListScreenImpl(artist, isBackFromSong, onSuccess)