package jatx.russianrocksongbook.view

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import jatx.russianrocksongbook.navigation.AppNavigator
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.textsearch.api.view.TextSearchListScreen
import jatx.russianrocksongbook.textsearch.api.view.TextSearchSongTextScreen
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
fun CurrentScreen() {
    val navController = rememberNavController()
    AppNavigator.injectNavController(navController) {
        CommonViewModel.getStoredInstance()?.submitAction(Back(true))
    }

    // view models survive popBackStack thereby next lines:
    val commonViewModel = CommonViewModel.getInstance()
    val localViewModel = LocalViewModel.getInstance()
    val voiceCommandViewModel = VoiceCommandViewModel.getInstance()
    val cloudViewModel = CloudViewModel.getInstance()
    val textSearchViewModel = TextSearchViewModel.getInstance()

    if (CommonViewModel.needReset) {
        commonViewModel.resetState()
        localViewModel.resetState()
        voiceCommandViewModel.resetState()
        cloudViewModel.resetState()
        textSearchViewModel.resetState()
        CommonViewModel.needReset = false
    }

    NavHost(
        navController,
        startDestination = ScreenVariant.Start,
        enterTransition = { EnterTransition.None },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable<ScreenVariant.Start> {
            StartScreen()
        }

        composable<ScreenVariant.SongList> { backStackEntry ->
            val route: ScreenVariant.SongList = backStackEntry.toRoute()
            SongListScreen(
                artist = route.artist,
                isBackFromSomeScreen = route.isBackFromSomeScreen
            )
        }

        composable<ScreenVariant.Favorite> { backStackEntry ->
            val route: ScreenVariant.Favorite = backStackEntry.toRoute()
            SongListScreen(
                artist = ARTIST_FAVORITE,
                isBackFromSomeScreen = route.isBackFromSomeScreen
            )
        }

        composable<ScreenVariant.SongText> { backStackEntry ->
            val route: ScreenVariant.SongText = backStackEntry.toRoute()
            SongTextScreen(
                position = route.position,
                randomKey = route.randomKey
            )
        }

        composable<ScreenVariant.SongTextByArtistAndTitle> { backStackEntry ->
            val route: ScreenVariant.SongTextByArtistAndTitle = backStackEntry.toRoute()
            SongListScreen(
                artist = route.artist,
                songTitleToPass = route.title
            )
        }

        composable<ScreenVariant.CloudSearch> { backStackEntry ->
            val route: ScreenVariant.CloudSearch = backStackEntry.toRoute()
            CloudSearchScreen(
                randomKey = route.randomKey,
                isBackFromSong = route.isBackFromSong
            )
        }

        composable<ScreenVariant.TextSearchList> { backStackEntry ->
            val route: ScreenVariant.TextSearchList = backStackEntry.toRoute()
            TextSearchListScreen(
                randomKey = route.randomKey,
                isBackFromSong = route.isBackFromSong
            )
        }

        composable<ScreenVariant.CloudSongText> { backStackEntry ->
            val route: ScreenVariant.CloudSongText = backStackEntry.toRoute()
            CloudSongTextScreen(
                position = route.position
            )
        }

        composable<ScreenVariant.TextSearchSongText> { backStackEntry ->
            val route: ScreenVariant.TextSearchSongText = backStackEntry.toRoute()
            TextSearchSongTextScreen(
                position = route.position,
                randomkey = route.randomKey
            )
        }

        composable<ScreenVariant.AddArtist> {
            AddArtistScreen()
        }

        composable<ScreenVariant.AddSong> {
            AddSongScreen()
        }

        composable<ScreenVariant.Donation> {
            DonationScreen()
        }

        composable<ScreenVariant.Settings> {
            SettingsScreen()
        }
    }
}