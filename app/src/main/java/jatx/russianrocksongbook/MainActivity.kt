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
import jatx.russianrocksongbook.addartist.viewmodel.AddArtistViewModel
import jatx.russianrocksongbook.cloudsongs.viewmodel.CloudViewModel
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.helpers.*
import jatx.russianrocksongbook.localsongs.viewmodel.LocalViewModel
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.preferences.Orientation
import jatx.russianrocksongbook.model.preferences.Settings
import jatx.russianrocksongbook.model.version.Version
import jatx.russianrocksongbook.start.viewmodel.StartViewModel
import jatx.russianrocksongbook.view.CurrentScreen
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.voicecommands.helpers.VoiceCommandHelper
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
    @Inject
    lateinit var voiceCommandHelper: VoiceCommandHelper

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
            withContext(Dispatchers.Main) {
                val startViewModel: StartViewModel by viewModels()
                startViewModel.asyncInit()
            }
        }
    }

    @Inject
    fun initActions() {
        Log.e("inject init", "actions")
        val mvvmViewModel: MvvmViewModel by viewModels()
        mvvmViewModel.callbacks.onRestartApp = ::restartApp
        mvvmViewModel.callbacks.onReviewApp = ::reviewApp
        mvvmViewModel.callbacks.onShowDevSite = ::showDevSite
        mvvmViewModel.callbacks.onOpenYandexMusic = musicHelper::openYandexMusic
        mvvmViewModel.callbacks.onOpenVkMusic = musicHelper::openVkMusic
        mvvmViewModel.callbacks.onOpenYoutubeMusic = musicHelper::openYoutubeMusic
        mvvmViewModel.callbacks.onAddSongsFromDir = ::addSongsFromDir
        mvvmViewModel.callbacks.onPurchaseItem = donationHelper::purchaseItem
        mvvmViewModel.callbacks.onCloudSearchScreenSelected = ::initCloudSearch
        mvvmViewModel.callbacks.onArtistSelected = ::selectArtist
        mvvmViewModel.callbacks.onSongByArtistAndTitleSelected =
            ::selectSongByArtistAndTitle
        mvvmViewModel.callbacks.onSpeechRecognize = ::speechRecognize
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

    private fun speechRecognize() {
        val localViewModel: LocalViewModel by viewModels()
        voiceCommandHelper.recognizeVoiceCommand {
            localViewModel.parseVoiceCommand(it)
        }
    }

    private fun showDevSite() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://tabatsky.ru")
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
        val addArtistViewModel: AddArtistViewModel by viewModels()
        addSongsFromDirHelper.addSongsFromDir(
            onPickedDirReturned = addArtistViewModel::copySongsFromDirToRepoWithPickedDir,
            onPathReturned = addArtistViewModel::copySongsFromDirToRepoWithPath
        )
    }

    private fun initCloudSearch() = runOnUiThread {
        val cloudViewModel: CloudViewModel by viewModels()
        cloudViewModel.cloudSearch("", OrderBy.BY_ID_DESC)
    }

    private fun selectArtist(artist: String) = runOnUiThread {
        val localViewModel: LocalViewModel by viewModels()
        localViewModel.selectArtist(artist)
    }

    private fun selectSongByArtistAndTitle(artist: String, title: String) =
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