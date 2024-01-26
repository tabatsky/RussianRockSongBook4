package jatx.russianrocksongbook.cloudsongs.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch.CloudSearchScreenImpl

@Composable
fun CloudSearchScreen(randomKey: Int, isBackFromSong: Boolean) =
    CloudSearchScreenImpl(randomKey, isBackFromSong)