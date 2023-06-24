package jatx.russianrocksongbook.view

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
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.donation.api.view.DonationScreen
import jatx.russianrocksongbook.localsongs.api.view.SongListScreen
import jatx.russianrocksongbook.localsongs.api.view.SongTextScreen
import jatx.russianrocksongbook.settings.api.view.SettingsScreen
import jatx.russianrocksongbook.start.api.view.StartScreen
import jatx.russianrocksongbook.navigation.NavControllerHolder
import jatx.russianrocksongbook.navigation.argArtist
import jatx.russianrocksongbook.navigation.argIsBackFromSong
import jatx.russianrocksongbook.navigation.argPosition
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

@Composable
fun CurrentScreen() {
    val navController = rememberNavController()
    NavControllerHolder.injectNavController(navController)

    NavHost(navController, startDestination = destinationStart) {

        composable(destinationStart) {
            StartScreen()
        }

        composable(
            "$destinationSongList/{$argArtist}/{$argIsBackFromSong}",
            arguments = listOf(
                navArgument(argArtist) { type = NavType.StringType },
                navArgument(argIsBackFromSong) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            SongListScreen(
                artist = backStackEntry.arguments?.getString(argArtist)
                    ?: throw IllegalArgumentException("No such argument"),
                isBackFromSong = backStackEntry.arguments?.getBoolean(argIsBackFromSong)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationFavorite/{$argIsBackFromSong}",
            arguments = listOf(
                navArgument(argIsBackFromSong) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            SongListScreen(
                artist = ARTIST_FAVORITE,
                isBackFromSong = backStackEntry.arguments?.getBoolean(argIsBackFromSong)
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
                passToSongWithTitle = backStackEntry.arguments?.getString(argTitle)
                    ?: throw IllegalArgumentException("No such argument")
            )
        }

        composable(
            "$destinationCloudSearch/{$argIsBackFromSong}",
            arguments = listOf(
                navArgument(argIsBackFromSong) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            CloudSearchScreen(
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