package jatx.russianrocksongbook.addartist.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistState
import jatx.russianrocksongbook.commonview.theme.LightTheme

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun AddArtistScreenImplPreviewLandscapeLight() {
    val addArtistStateState = remember { mutableStateOf(AddArtistState.initial()) }

    LightTheme {
        AddArtistScreenImplContent(
            addArtistStateState = addArtistStateState,
            submitAction = {}
        )
    }
}