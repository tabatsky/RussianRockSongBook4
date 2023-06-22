package jatx.russianrocksongbook.view

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
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
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.viewmodel.view.argArtist
import jatx.russianrocksongbook.viewmodel.view.argIsBackFromSong
import jatx.russianrocksongbook.viewmodel.view.destinationSongList
import jatx.russianrocksongbook.viewmodel.view.destinationStart
import jatx.russianrocksongbook.viewmodel.view.injectNavController
import java.lang.IllegalStateException

@Composable
fun CurrentScreen() {
    val commonViewModel: CommonViewModel = viewModel()
    when (
        val screenVariant =
            commonViewModel.currentScreenVariant.collectAsState().value.also {
                Log.e("screenVariant", it.toString())
            }
    ) {
        is CurrentScreenVariant.START -> StartScreen()
        is CurrentScreenVariant.SONG_LIST ->
            SongListScreen(
                artist = screenVariant.artist,
                isBackFromSong = screenVariant.isBackFromSong
            )
        is CurrentScreenVariant.FAVORITE ->
            SongListScreen(
                artist = ARTIST_FAVORITE,
                isBackFromSong = screenVariant.isBackFromSong
            )
        is CurrentScreenVariant.SONG_TEXT ->
            SongTextScreen(
                artist = screenVariant.artist,
                position = screenVariant.position
            )
        is CurrentScreenVariant.SONG_TEXT_BY_ARTIST_AND_TITLE ->
            SongListScreen(
                artist = screenVariant.artist,
                passToSongWithTitle = screenVariant.title
            )
        is CurrentScreenVariant.SETTINGS -> SettingsScreen()
        is CurrentScreenVariant.CLOUD_SEARCH -> CloudSearchScreen(
            isBackFromSong = screenVariant.isBackFromSong
        )
        is CurrentScreenVariant.CLOUD_SONG_TEXT -> CloudSongTextScreen(
            position = screenVariant.position
        )
        is CurrentScreenVariant.ADD_ARTIST -> AddArtistScreen()
        is CurrentScreenVariant.ADD_SONG -> AddSongScreen()
        is CurrentScreenVariant.DONATION -> DonationScreen()
    }
}

@Composable
fun CurrentScreenNew() {
    val navController = rememberNavController()
    injectNavController(navController)
    NavHost(navController, startDestination = destinationStart) {
        composable(destinationStart) {
            StartScreen()
        }
        composable(
            "$destinationSongList/{artist}/{isBackFromSong}",
            arguments = listOf(
                navArgument(argArtist) { type = NavType.StringType },
                navArgument(argIsBackFromSong) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            SongListScreen(
                artist = backStackEntry.arguments?.getString(argArtist)
                    ?: throw IllegalStateException(),
                isBackFromSong = backStackEntry.arguments?.getBoolean(argIsBackFromSong)
                    ?: throw IllegalStateException()
            )
        }
    }
}