package jatx.russianrocksongbook

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
import androidx.compose.foundation.ExperimentalFoundationApi
import dagger.hilt.android.AndroidEntryPoint
import jatx.russianrocksongbook.addartist.api.ext.copySongsFromDirToRepoWithPath
import jatx.russianrocksongbook.addartist.api.ext.copySongsFromDirToRepoWithPickedDir
import jatx.russianrocksongbook.cloudsongs.api.ext.initCloudSearch
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.domain.usecase.SendCrashUseCase
import jatx.russianrocksongbook.helpers.api.AddSongsFromDirHelper
import jatx.russianrocksongbook.helpers.api.DonationHelper
import jatx.russianrocksongbook.helpers.api.MusicHelper
import jatx.russianrocksongbook.localsongs.api.ext.parseVoiceCommand
import jatx.russianrocksongbook.localsongs.api.ext.selectArtist
import jatx.russianrocksongbook.localsongs.api.ext.selectSongByArtistAndTitle
import jatx.russianrocksongbook.preferences.api.Orientation
import jatx.russianrocksongbook.preferences.api.SettingsRepository
import jatx.russianrocksongbook.domain.models.interfaces.Version
import jatx.russianrocksongbook.start.api.ext.asyncInit
import jatx.russianrocksongbook.view.CurrentScreen
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.voicecommands.api.VoiceCommandHelper
import kotlinx.coroutines.*
import java.lang.NullPointerException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var donationHelper: DonationHelper
    @Inject
    lateinit var musicHelper: MusicHelper
    @Inject
    lateinit var addSongsFromDirHelper: AddSongsFromDirHelper
    @Inject
    lateinit var voiceCommandHelper: VoiceCommandHelper
    @Inject
    lateinit var sendCrashUseCase: SendCrashUseCase
    @Inject
    lateinit var version: Version

    @ExperimentalFoundationApi
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = when (settingsRepository.orientation) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        setContent {
            CurrentScreen()
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        asyncInit()
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
        AppDebug.setAppCrashHandler(sendCrashUseCase, version)
    }

    override fun onBackPressed() {
        val mvvmViewModel: MvvmViewModel by viewModels()
        mvvmViewModel.back {
            finish()
        }
    }

    private fun restartApp() {
        val packageManager: PackageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
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
        addSongsFromDirHelper.addSongsFromDir(
            onPickedDirReturned = ::copySongsFromDirToRepoWithPickedDir,
            onPathReturned = ::copySongsFromDirToRepoWithPath
        )
    }

    private fun speechRecognize() {
        voiceCommandHelper.recognizeVoiceCommand {
            parseVoiceCommand(it)
        }
    }

}