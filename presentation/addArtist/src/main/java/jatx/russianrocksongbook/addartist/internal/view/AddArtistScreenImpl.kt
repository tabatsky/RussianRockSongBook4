package jatx.russianrocksongbook.addartist.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel

@Composable
internal fun AddArtistScreenImpl() {
    val addArtistViewModel = AddArtistViewModel.getInstance()

    val submitAction = addArtistViewModel::submitAction
    val addArtistStateState = addArtistViewModel.addArtistStateFlow.collectAsState()

    AddArtistScreenImplContent(
        addArtistStateState = addArtistStateState,
        submitAction = submitAction
    )
}
