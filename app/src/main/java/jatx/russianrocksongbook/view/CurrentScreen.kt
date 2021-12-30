package jatx.russianrocksongbook.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.addartist.api.view.AddArtistScreen
import jatx.russianrocksongbook.addsong.api.view.AddSongScreen
import jatx.russianrocksongbook.cloudsongs.api.view.CloudSearchScreen
import jatx.russianrocksongbook.cloudsongs.api.view.CloudSongTextScreen
import jatx.russianrocksongbook.donation.api.view.DonationScreen
import jatx.russianrocksongbook.localsongs.api.view.SongListScreen
import jatx.russianrocksongbook.localsongs.api.view.SongTextScreen
import jatx.russianrocksongbook.settings.api.view.SettingsScreen
import jatx.russianrocksongbook.start.api.view.StartScreen
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.CommonViewModel

@ExperimentalFoundationApi
@Composable
fun CurrentScreen() {
    val commonViewModel: CommonViewModel = viewModel()
    when (commonViewModel.currentScreenVariant.collectAsState().value) {
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
