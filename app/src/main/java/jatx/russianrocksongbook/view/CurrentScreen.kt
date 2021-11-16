package jatx.russianrocksongbook.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun CurrentScreen(mvvmViewModel: MvvmViewModel) {
    when (mvvmViewModel.currentScreenVariant.collectAsState().value) {
        CurrentScreenVariant.START -> StartScreen(mvvmViewModel = mvvmViewModel)
        CurrentScreenVariant.SONG_LIST, CurrentScreenVariant.FAVORITE -> SongListScreen(
            mvvmViewModel = mvvmViewModel,
        )
        CurrentScreenVariant.SONG_TEXT -> SongTextScreen(
            mvvmViewModel = mvvmViewModel
        )
        CurrentScreenVariant.SETTINGS -> SettingsScreen(
            mvvmViewModel = mvvmViewModel
        )
        CurrentScreenVariant.CLOUD_SEARCH -> CloudSearchScreen(
            mvvmViewModel = mvvmViewModel
        )
        CurrentScreenVariant.CLOUD_SONG_TEXT -> CloudSongTextScreen(
            mvvmViewModel = mvvmViewModel
        )
        CurrentScreenVariant.ADD_ARTIST -> AddArtistScreen(
            mvvmViewModel = mvvmViewModel
        )
        CurrentScreenVariant.ADD_SONG -> AddSongScreen(
            mvvmViewModel = mvvmViewModel
        )
        CurrentScreenVariant.DONATION -> DonationScreen(
            mvvmViewModel = mvvmViewModel
        )
    }
}

enum class CurrentScreenVariant {
    START,
    SONG_LIST,
    FAVORITE,
    SONG_TEXT,
    ADD_ARTIST,
    ADD_SONG,
    CLOUD_SEARCH,
    CLOUD_SONG_TEXT,
    DONATION,
    SETTINGS
}