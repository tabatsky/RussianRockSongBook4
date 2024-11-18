package jatx.russianrocksongbook.addartist.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistState

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun AddArtistScreenImplPreviewLandscape() {
    val addArtistStateState = remember { mutableStateOf(AddArtistState.initial()) }

    AddArtistScreenImplContent(
        addArtistStateState = addArtistStateState,
        submitAction = {}
    )
}