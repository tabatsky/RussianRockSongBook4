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
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.helpers.AddSongsFromDirHelper
import jatx.russianrocksongbook.helpers.DonationHelper
import jatx.russianrocksongbook.helpers.MusicHelper
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.preferences.Orientation
import jatx.russianrocksongbook.model.preferences.Settings
import jatx.russianrocksongbook.model.version.Version
import jatx.russianrocksongbook.view.*
import jatx.russianrocksongbook.cloudsongs.viewmodel.CloudViewModel
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.localsongs.viewmodel.LocalViewModel
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settings: Settings
    @Inject
    lateinit var donationHelper: DonationHelper
    @Inject
    lateinit var musicHelper: MusicHelper
    @Inject
    lateinit var addSongsFromDirHelper: AddSongsFromDirHelper

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = when (settings.orientation) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        setContent {
            CurrentScreen()
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val mvvmViewModel: MvvmViewModel by viewModels()
                mvvmViewModel.asyncInit()
            }
        }
    }

    @Inject
    fun initActions() {
        Log.e("inject init", "actions")
        val mvvmViewModel: MvvmViewModel by viewModels()
        mvvmViewModel.actions.onRestartApp = ::restartApp
        mvvmViewModel.actions.onReviewApp = ::reviewApp
        mvvmViewModel.actions.onShowDevSite = ::showDevSite
        mvvmViewModel.actions.onOpenYandexMusic = musicHelper::openYandexMusic
        mvvmViewModel.actions.onOpenVkMusic = musicHelper::openVkMusic
        mvvmViewModel.actions.onOpenYoutubeMusic = musicHelper::openYoutubeMusic
        mvvmViewModel.actions.onAddSongsFromDir = ::addSongsFromDir
        mvvmViewModel.actions.onPurchaseItem = donationHelper::purchaseItem
        mvvmViewModel.actions.onCloudSearchScreenSelected = {
            runOnUiThread {
                val cloudViewModel: CloudViewModel by viewModels()
                cloudViewModel.cloudSearch("", OrderBy.BY_ID_DESC)
                cloudViewModel.selectCloudSong(0)
            }
        }
        mvvmViewModel.actions.onArtistSelected = {
            runOnUiThread {
                val localViewModel: LocalViewModel by viewModels()
                localViewModel.selectArtist(it)
            }
        }
        mvvmViewModel.actions.onSongByArtistAndTitleSelected = { artist, title ->
            runOnUiThread {
                val localViewModel: LocalViewModel by viewModels()
                localViewModel.selectArtist(
                    artist = artist,
                    forceOnSuccess = true,
                    onSuccess = {
                        val position = localViewModel
                            .currentSongList
                            .value
                            .map { it.title }
                            .indexOf(title)
                        localViewModel.selectSong(position)
                        localViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
                    }
                )
            }
        }
    }

    @Inject
    fun initAppDebug() {
        Log.e("inject init", "AppDebug")
        val mvvmViewModel: MvvmViewModel by viewModels()
        AppDebug.setAppCrashHandler(mvvmViewModel.songBookAPIAdapter)
    }

    @Inject
    fun initVersion(@ApplicationContext context: Context) {
        Log.e("inject init", "Version")
        Version.init(context)
    }

    override fun onBackPressed() {
        val mvvmViewModel: MvvmViewModel by viewModels()
        mvvmViewModel.back {
            finish()
        }
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

    private fun addSongsFromDir() {
        val mvvmViewModel: MvvmViewModel by viewModels()
        addSongsFromDirHelper.addSongsFromDir(
            onPickedDirReturned = mvvmViewModel::copySongsFromDirToRepoWithPickedDir,
            onPathReturned = mvvmViewModel::copySongsFromDirToRepoWithPath
        )
    }
}