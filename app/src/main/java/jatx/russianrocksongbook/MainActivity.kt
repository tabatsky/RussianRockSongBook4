package jatx.russianrocksongbook

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import jatx.russianrocksongbook.data.*
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.helpers.AddSongsFromDirHelper
import jatx.russianrocksongbook.helpers.DonationHelper
import jatx.russianrocksongbook.helpers.MusicHelper
import jatx.russianrocksongbook.preferences.Orientation
import jatx.russianrocksongbook.preferences.Settings
import jatx.russianrocksongbook.preferences.Version
import jatx.russianrocksongbook.view.*
import jatx.russianrocksongbook.viewmodel.CurrentScreen
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settings: Settings
    @Inject
    lateinit var mvvmViewModel: MvvmViewModel
    @Inject
    lateinit var donationHelper: DonationHelper
    @Inject
    lateinit var musicHelper: MusicHelper
    @Inject
    lateinit var addSongsFromDirHelper: AddSongsFromDirHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = when (settings.orientation) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        setContent {
            when (mvvmViewModel.currentScreen.collectAsState().value) {
                CurrentScreen.STUB -> StubScreen(mvvmViewModel = mvvmViewModel)
                CurrentScreen.SONG_LIST, CurrentScreen.FAVORITE -> SongListScreen(
                    mvvmViewModel = mvvmViewModel,
                )
                CurrentScreen.SONG_TEXT -> SongTextScreen(
                    mvvmViewModel = mvvmViewModel
                )
                CurrentScreen.SETTINGS -> SettingsScreen(
                    mvvmViewModel = mvvmViewModel
                )
                CurrentScreen.CLOUD_SEARCH -> CloudSearchScreen(
                    mvvmViewModel = mvvmViewModel
                )
                CurrentScreen.CLOUD_SONG_TEXT -> CloudSongTextScreen(
                    mvvmViewModel = mvvmViewModel
                )
                CurrentScreen.ADD_ARTIST -> AddArtistScreen(
                    mvvmViewModel = mvvmViewModel
                )
                CurrentScreen.ADD_SONG -> AddSongScreen(
                    mvvmViewModel = mvvmViewModel
                )
                CurrentScreen.DONATION -> DonationScreen(
                    mvvmViewModel = mvvmViewModel
                )
            }
        }

        mvvmViewModel.onRestartApp = ::restartApp
        mvvmViewModel.onReviewApp = ::reviewApp
        mvvmViewModel.onShowDevSite = ::showDevSite
        mvvmViewModel.onOpenYandexMusic = musicHelper::openYandexMusic
        mvvmViewModel.onOpenVkMusic = musicHelper::openVkMusic
        mvvmViewModel.onOpenYoutubeMusic = musicHelper::openYoutubeMusic
        mvvmViewModel.onAddSongsFromDir = ::addSongsFromDir
        mvvmViewModel.onPurchaseItem = donationHelper::purchaseItem

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                mvvmViewModel.asyncInit()
            }
        }
    }

    @Inject
    fun initAppDebug(songBookAPIAdapter: SongBookAPIAdapter) {
        Log.e("inject init", "AppDebug")
        AppDebug.setAppCrashHandler(songBookAPIAdapter)
    }

    @Inject
    fun initVersion(@ApplicationContext context: Context) {
        Log.e("inject init", "Version")
        Version.init(context)
    }

    override fun onBackPressed() = mvvmViewModel.back {
        finish()
    }

    private fun restartApp() {
        val packageManager: PackageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(getPackageName())
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    private fun showDevSite() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://tabatsky.ru"
                )
            )
        )
    }

    private fun reviewApp() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=jatx.russianrocksongbook")
            )
        )
    }

    private fun addSongsFromDir() = addSongsFromDirHelper.addSongsFromDir(
        onPickedDirReturned = mvvmViewModel::copySongsFromDirToRepoWithPickedDir,
        onPathReturned = mvvmViewModel::copySongsFromDirToRepoWithPath
    )
}