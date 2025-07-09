package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.localsongs.internal.view.songtext.SongTextScreenImpl

@Composable
fun SongTextScreen(position: Int, randomKey: Int) =
    SongTextScreenImpl(position, randomKey)