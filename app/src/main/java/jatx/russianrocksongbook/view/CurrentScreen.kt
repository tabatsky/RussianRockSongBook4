package jatx.russianrocksongbook.view

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import jatx.russianrocksongbook.navigation.argArtist
import jatx.russianrocksongbook.navigation.argCloudSearchRandomKey
import jatx.russianrocksongbook.navigation.argIsBackFromSomeScreen
import jatx.russianrocksongbook.navigation.argIsBackFromSong
import jatx.russianrocksongbook.navigation.argPosition
import jatx.russianrocksongbook.navigation.argTextSearchListRandomKey
import jatx.russianrocksongbook.navigation.argTitle
import jatx.russianrocksongbook.navigation.destinationAddArtist
import jatx.russianrocksongbook.navigation.destinationAddSong
import jatx.russianrocksongbook.navigation.destinationCloudSearch
import jatx.russianrocksongbook.navigation.destinationCloudSongText
import jatx.russianrocksongbook.navigation.destinationDonation
import jatx.russianrocksongbook.navigation.destinationFavorite
import jatx.russianrocksongbook.navigation.destinationSettings
import jatx.russianrocksongbook.navigation.destinationSongList
import jatx.russianrocksongbook.navigation.destinationSongText
import jatx.russianrocksongbook.navigation.destinationSongTextByArtistAndTitle
import jatx.russianrocksongbook.navigation.destinationStart
import jatx.russianrocksongbook.navigation.destinationTextSearchList
import jatx.russianrocksongbook.textsearch.api.view.TextSearchListScreen
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
        startDestination = destinationStart,
        enterTransition = { EnterTransition.None },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable(destinationStart) {
            StartScreen()
        }

        composable(
            "$destinationSongList/{$argArtist}/{$argIsBackFromSomeScreen}",
            arguments = listOf(
                navArgument(argArtist) { type = NavType.StringType },
                navArgument(argIsBackFromSomeScreen) { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            SongListScreen(
                artist = backStackEntry.arguments?.getString(argArtist)
                    ?: throw IllegalArgumentException("No such argument"),
                isBackFromSomeScreen = backStackEntry.arguments?.getBoolean(argIsBackFromSomeScreen)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationFavorite/{$argIsBackFromSomeScreen}",
            arguments = listOf(
                navArgument(argIsBackFromSomeScreen) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            SongListScreen(
                artist = ARTIST_FAVORITE,
                isBackFromSomeScreen = backStackEntry.arguments?.getBoolean(argIsBackFromSomeScreen)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationSongText/{$argArtist}/{$argPosition}",
            arguments = listOf(
                navArgument(argArtist) { type = NavType.StringType },
                navArgument(argPosition) { type = NavType.IntType },
            )
        ) { backStackEntry ->
            SongTextScreen(
                artist = backStackEntry.arguments?.getString(argArtist)
                    ?: throw IllegalArgumentException("No such argument"),
                position = backStackEntry.arguments?.getInt(argPosition)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationSongTextByArtistAndTitle/{$argArtist}/{$argTitle}",
            arguments = listOf(
                navArgument(argArtist) { type = NavType.StringType },
                navArgument(argTitle) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            SongListScreen(
                artist = backStackEntry.arguments?.getString(argArtist)
                    ?: throw IllegalArgumentException("No such argument"),
                songTitleToPass = backStackEntry.arguments?.getString(argTitle)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationCloudSearch/{$argCloudSearchRandomKey}/{$argIsBackFromSong}",
            arguments = listOf(
                navArgument(argCloudSearchRandomKey) { type = NavType.IntType },
                navArgument(argIsBackFromSong) { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            CloudSearchScreen(
                randomKey = backStackEntry.arguments?.getInt(argCloudSearchRandomKey)
                    ?: throw IllegalArgumentException("No such argument"),
                isBackFromSong = backStackEntry.arguments?.getBoolean(argIsBackFromSong)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationTextSearchList/{$argTextSearchListRandomKey}/{$argIsBackFromSong}",
            arguments = listOf(
                navArgument(argTextSearchListRandomKey) { type = NavType.IntType },
                navArgument(argIsBackFromSong) { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            TextSearchListScreen(
                randomKey = backStackEntry.arguments?.getInt(argTextSearchListRandomKey)
                    ?: throw IllegalArgumentException("No such argument"),
                isBackFromSong = backStackEntry.arguments?.getBoolean(argIsBackFromSong)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationCloudSongText/{$argPosition}",
            arguments = listOf(
                navArgument(argPosition) { type = NavType.IntType },
            )
        ) { backStackEntry ->
            CloudSongTextScreen(
                position = backStackEntry.arguments?.getInt(argPosition)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(destinationAddArtist) {
            AddArtistScreen()
        }

        composable(destinationAddSong) {
            AddSongScreen()
        }

        composable(destinationDonation) {
            DonationScreen()
        }

        composable(destinationSettings) {
            SettingsScreen()
        }
    }
}