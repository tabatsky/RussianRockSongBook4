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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import jatx.russianrocksongbook.addartist.api.methods.copySongsFromDirToRepoWithPath
import jatx.russianrocksongbook.addartist.api.methods.copySongsFromDirToRepoWithPickedDir
import jatx.russianrocksongbook.addsongsfromdirhelper.api.AddSongsFromDirHelper
import jatx.russianrocksongbook.commonviewmodel.Back
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.domain.usecase.cloud.SendCrashUseCase
import jatx.russianrocksongbook.musichelper.api.MusicHelper
import jatx.russianrocksongbook.localsongs.api.methods.parseAndExecuteVoiceCommand
import jatx.russianrocksongbook.domain.models.appcrash.Version
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.donationhelper.api.DonationHelper
import jatx.russianrocksongbook.localsongs.api.methods.selectArtist
import jatx.russianrocksongbook.localsongs.api.methods.selectSongByArtistAndTitle
import jatx.russianrocksongbook.navigation.NavControllerHolder
import jatx.russianrocksongbook.view.CurrentScreen
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = when (settingsRepository.orientation) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        setContent {
            ActionsInjector()
            BackHandler(true) {
                back()
            }
            CurrentScreen()
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun back() = CommonViewModel.getStoredInstance()?.submitAction(Back)

    fun clean() {
        CommonViewModel.clearStorage()
        viewModelStore.clear()
        NavControllerHolder.cleanNavController()
    }

    @Composable
    fun ActionsInjector() {
        Log.e("inject init", "actions")
        val commonViewModel = CommonViewModel.getInstance()
        commonViewModel.callbacks.onRestartApp = ::restartApp
        commonViewModel.callbacks.onReviewApp = ::reviewApp
        commonViewModel.callbacks.onShowDevSite = ::showDevSite
        commonViewModel.callbacks.onOpenYandexMusic = musicHelper::openYandexMusic
        commonViewModel.callbacks.onOpenVkMusic = musicHelper::openVkMusic
        commonViewModel.callbacks.onOpenYoutubeMusic = musicHelper::openYoutubeMusic
        commonViewModel.callbacks.onAddSongsFromDir = ::addSongsFromDir
        commonViewModel.callbacks.onPurchaseItem = donationHelper::purchaseItem
        commonViewModel.callbacks.onArtistSelected = ::selectArtist
        commonViewModel.callbacks.onSongByArtistAndTitleSelected =
            ::selectSongByArtistAndTitle
        commonViewModel.callbacks.onSpeechRecognize = ::speechRecognize
        commonViewModel.callbacks.onFinish = {
            clean()
            finish()
        }
    }

    @Inject
    fun initAppDebug() {
        Log.e("inject init", "AppDebug")
        AppDebug.setAppCrashHandler(sendCrashUseCase, version)
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