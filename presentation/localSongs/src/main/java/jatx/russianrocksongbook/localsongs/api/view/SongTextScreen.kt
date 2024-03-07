package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.localsongs.internal.view.songtext.SongTextScreenImpl

@Composable
fun SongTextScreen(artist: String, position: Int) =
    SongTextScreenImpl(artist, position)