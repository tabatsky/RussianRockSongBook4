package jatx.russianrocksongbook.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.cloudsongs.view.CloudSearchScreen
import jatx.russianrocksongbook.cloudsongs.view.CloudSongTextScreen
import jatx.russianrocksongbook.localsongs.view.SongListScreen
import jatx.russianrocksongbook.localsongs.view.SongTextScreen
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun CurrentScreen(mvvmViewModel: MvvmViewModel = viewModel()) {
    when (mvvmViewModel.currentScreenVariant.collectAsState().value) {
        CurrentScreenVariant.START -> StartScreen()
        CurrentScreenVariant.SONG_LIST, CurrentScreenVariant.FAVORITE ->
            SongListScreen()
        CurrentScreenVariant.SONG_TEXT -> SongTextScreen()
        CurrentScreenVariant.SETTINGS -> SettingsScreen()
        CurrentScreenVariant.CLOUD_SEARCH -> CloudSearchScreen()
        CurrentScreenVariant.CLOUD_SONG_TEXT -> CloudSongTextScreen()
        CurrentScreenVariant.ADD_ARTIST -> AddArtistScreen()
        CurrentScreenVariant.ADD_SONG -> AddSongScreen()
        CurrentScreenVariant.DONATION -> DonationScreen()
    }
}
