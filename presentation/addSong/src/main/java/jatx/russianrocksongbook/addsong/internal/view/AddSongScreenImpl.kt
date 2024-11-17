package jatx.russianrocksongbook.addsong.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import jatx.russianrocksongbook.addsong.internal.viewmodel.AddSongViewModel

@Composable
internal fun AddSongScreenImpl() {
    val addSongViewModel = AddSongViewModel.getInstance()

    val artistState = addSongViewModel.artist
    val titleState = addSongViewModel.title
    val textState = addSongViewModel.text
    val addSongStateState = addSongViewModel.addSongStateFlow.collectAsState()
    val submitAction = addSongViewModel::submitAction
    val submitEffect = addSongViewModel::submitEffect

    AddSongScreenImplContent(
        artistState = artistState,
        titleState = titleState,
        textState = textState,
        addSongStateState = addSongStateState,
        submitAction = submitAction,
        submitEffect = submitEffect
    )
}

