package jatx.russianrocksongbook.addsong.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.addsong.internal.viewmodel.AddSongState

@Preview
@Composable
internal fun AddSongScreenImplPreview() {
    val artistState = remember { mutableStateOf("") }
    val titleState = remember { mutableStateOf("") }
    val textState = remember { mutableStateOf("") }
    val addSongStateState = remember { mutableStateOf(AddSongState.initial()) }

    AddSongScreenImplContent(
        artistState = artistState,
        titleState = titleState,
        textState = textState,
        addSongStateState = addSongStateState,
        submitAction = {},
        submitEffect = {}
    )
}