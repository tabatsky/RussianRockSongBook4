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
import jatx.russianrocksongbook.navigation.AddArtistRoute
import jatx.russianrocksongbook.navigation.AddSongRoute
import jatx.russianrocksongbook.settings.api.view.SettingsScreen
import jatx.russianrocksongbook.start.api.view.StartScreen
import jatx.russianrocksongbook.navigation.AppNavigator
import jatx.russianrocksongbook.navigation.CloudSearchRoute
import jatx.russianrocksongbook.navigation.CloudSongTextRoute
import jatx.russianrocksongbook.navigation.DonationRoute
import jatx.russianrocksongbook.navigation.FavoriteRoute
import jatx.russianrocksongbook.navigation.SettingsRoute
import jatx.russianrocksongbook.navigation.SongListRoute
import jatx.russianrocksongbook.navigation.SongTextByArtistAndTitleRoute
import jatx.russianrocksongbook.navigation.SongTextRoute
import jatx.russianrocksongbook.navigation.StartRoute
import jatx.russianrocksongbook.navigation.TextSearchListRoute
import jatx.russianrocksongbook.navigation.TextSearchSongTextRoute
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
        startDestination = StartRoute,
        enterTransition = { EnterTransition.None },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable<StartRoute> {
            StartScreen()
        }

        composable<SongListRoute> { backStackEntry ->
            val route: SongListRoute = backStackEntry.toRoute()
            SongListScreen(
                artist = route.artist,
                isBackFromSomeScreen = route.isBackFromSomeScreen
            )
        }

        composable<FavoriteRoute> { backStackEntry ->
            val route: FavoriteRoute = backStackEntry.toRoute()
            SongListScreen(
                artist = ARTIST_FAVORITE,
                isBackFromSomeScreen = route.isBackFromSomeScreen
            )
        }

        composable<SongTextRoute> { backStackEntry ->
            val route: SongTextRoute = backStackEntry.toRoute()
            SongTextScreen(
                artist = route.artist,
                position = route.position
            )
        }

        composable<SongTextByArtistAndTitleRoute> { backStackEntry ->
            val route: SongTextByArtistAndTitleRoute = backStackEntry.toRoute()
            SongListScreen(
                artist = route.artist,
                songTitleToPass = route.title
            )
        }

        composable<CloudSearchRoute> { backStackEntry ->
            val route: CloudSearchRoute = backStackEntry.toRoute()
            CloudSearchScreen(
                randomKey = route.randomKey,
                isBackFromSong = route.isBackFromSong
            )
        }

        composable<TextSearchListRoute> { backStackEntry ->
            val route: TextSearchListRoute = backStackEntry.toRoute()
            TextSearchListScreen(
                randomKey = route.randomKey,
                isBackFromSong = route.isBackFromSong
            )
        }

        composable<CloudSongTextRoute> { backStackEntry ->
            val route: CloudSongTextRoute = backStackEntry.toRoute()
            CloudSongTextScreen(
                position = route.position
            )
        }

        composable<TextSearchSongTextRoute> { backStackEntry ->
            val route: TextSearchSongTextRoute = backStackEntry.toRoute()
            TextSearchSongTextScreen(
                position = route.position
            )
        }

        composable<AddArtistRoute> {
            AddArtistScreen()
        }

        composable<AddSongRoute> {
            AddSongScreen()
        }

        composable<DonationRoute> {
            DonationScreen()
        }

        composable<SettingsRoute> {
            SettingsScreen()
        }
    }
}