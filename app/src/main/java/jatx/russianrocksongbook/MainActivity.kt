package jatx.russianrocksongbook

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
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
import jatx.russianrocksongbook.localsongs.api.ext.parseAndExecuteVoiceCommand
import jatx.russianrocksongbook.localsongs.api.ext.selectArtist
import jatx.russianrocksongbook.localsongs.api.ext.selectSongByArtistAndTitle
import jatx.russianrocksongbook.preferences.api.Orientation
import jatx.russianrocksongbook.preferences.api.SettingsRepository
import jatx.russianrocksongbook.domain.models.interfaces.Version
import jatx.russianrocksongbook.start.api.ext.asyncInit
import jatx.russianrocksongbook.view.CurrentScreen
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.voicecommands.api.VoiceCommandHelper
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
        val commonViewModel: CommonViewModel by viewModels()
        commonViewModel.callbacks.onRestartApp = ::restartApp
        commonViewModel.callbacks.onReviewApp = ::reviewApp
        commonViewModel.callbacks.onShowDevSite = ::showDevSite
        commonViewModel.callbacks.onOpenYandexMusic = musicHelper::openYandexMusic
        commonViewModel.callbacks.onOpenVkMusic = musicHelper::openVkMusic
        commonViewModel.callbacks.onOpenYoutubeMusic = musicHelper::openYoutubeMusic
        commonViewModel.callbacks.onAddSongsFromDir = ::addSongsFromDir
        commonViewModel.callbacks.onPurchaseItem = donationHelper::purchaseItem
        commonViewModel.callbacks.onCloudSearchScreenSelected = ::initCloudSearch
        commonViewModel.callbacks.onArtistSelected = ::selectArtist
        commonViewModel.callbacks.onSongByArtistAndTitleSelected =
            ::selectSongByArtistAndTitle
        commonViewModel.callbacks.onSpeechRecognize = ::speechRecognize
    }

    @Inject
    fun initAppDebug() {
        Log.e("inject init", "AppDebug")
        AppDebug.setAppCrashHandler(sendCrashUseCase, version)
    }

    override fun onBackPressed() {
        val commonViewModel: CommonViewModel by viewModels()
        commonViewModel.back {
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
        voiceCommandHelper.recognizeVoiceCommand(
            onVoiceCommand =  {
                parseAndExecuteVoiceCommand(it)
            },
            onError = {
                Toast.makeText(
                    this,
                    getString(R.string.toast_speech_recognize_not_supported),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

}