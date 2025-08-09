package jatx.russianrocksongbook.view

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import jatx.russianrocksongbook.addartist.api.view.AddArtistScreen
import jatx.russianrocksongbook.addsong.api.view.AddSongScreen
import jatx.russianrocksongbook.cloudsongs.api.view.CloudSearchScreen
import jatx.russianrocksongbook.cloudsongs.api.view.CloudSongTextScreen
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.commonviewmodel.Back
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.donation.api.view.DonationScreen
import jatx.russianrocksongbook.localsongs.api.view.SongListScreen
import jatx.russianrocksongbook.localsongs.api.view.SongTextScreen
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel
import jatx.russianrocksongbook.settings.api.view.SettingsScreen
import jatx.russianrocksongbook.start.api.view.StartScreen
import jatx.russianrocksongbook.navigation.*
import jatx.russianrocksongbook.textsearch.api.view.TextSearchListScreen
import jatx.russianrocksongbook.textsearch.api.view.TextSearchSongTextScreen
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
fun CurrentScreen() {
    // view models survive popBackStack thereby next lines:
    val commonViewModel = CommonViewModel.getInstance()
    val localViewModel = LocalViewModel.getInstance()
    val voiceCommandViewModel = VoiceCommandViewModel.getInstance()
    val cloudViewModel = CloudViewModel.getInstance()
    val textSearchViewModel = TextSearchViewModel.getInstance()

    val backStack = rememberNavBackStack(EmptyScreenVariant, StartScreenVariant)
    LaunchedEffect(Unit) {
        commonViewModel.injectBackStack(backStack)
    }

    if (CommonViewModel.needReset) {
        commonViewModel.resetState()
        localViewModel.resetState()
        voiceCommandViewModel.resetState()
        cloudViewModel.resetState()
        textSearchViewModel.resetState()
        CommonViewModel.needReset = false
    }

    NavDisplay(
        backStack = backStack,
        onBack = { commonViewModel.submitAction(Back) },
        entryProvider = entryProvider {
            entry(EmptyScreenVariant) {}

            entry<StartScreenVariant> {
                StartScreen()
            }

            entry<SongListScreenVariant> { key ->
                SongListScreen(
                    artist = key.artist,
                    isBackFromSomeScreen = key.isBackFromSomeScreen
                )
            }

            entry<FavoriteScreenVariant> { key ->
                SongListScreen(
                    artist = ARTIST_FAVORITE,
                    isBackFromSomeScreen = key.isBackFromSomeScreen
                )
            }

            entry<SongTextScreenVariant> { key ->
                SongTextScreen(
                    position = key.position,
                    randomKey = key.randomKey
                )
            }

            entry<SongTextByArtistAndTitleScreenVariant> { key ->
                SongListScreen(
                    artist = key.artist,
                    songTitleToPass = key.title
                )
            }

            entry<CloudSearchScreenVariant> { key ->
                CloudSearchScreen(
                    randomKey = key.randomKey,
                    isBackFromSong = key.isBackFromSong
                )
            }

            entry<TextSearchListScreenVariant> { key ->
                TextSearchListScreen(
                    randomKey = key.randomKey,
                    isBackFromSong = key.isBackFromSong
                )
            }

            entry<CloudSongTextScreenVariant> { key ->
                CloudSongTextScreen(
                    position = key.position
                )
            }

            entry<TextSearchSongTextScreenVariant> { key ->
                TextSearchSongTextScreen(
                    position = key.position,
                    randomkey = key.randomKey
                )
            }

            entry<AddArtistScreenVariant> {
                AddArtistScreen()
            }

            entry<AddSongScreenVariant> {
                AddSongScreen()
            }

            entry<DonationScreenVariant> {
                DonationScreen()
            }

            entry<SettingsScreenVariant> {
                SettingsScreen()
            }
        },
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
    )
}