package jatx.russianrocksongbook

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.documentfile.provider.DocumentFile
import com.android.billingclient.api.*
import com.obsez.android.lib.filechooser.ChooserDialog
import dagger.hilt.android.AndroidEntryPoint
import jatx.russianrocksongbook.data.*
import jatx.russianrocksongbook.db.util.applySongPatches
import jatx.russianrocksongbook.db.util.deleteWrongArtists
import jatx.russianrocksongbook.db.util.deleteWrongSongs
import jatx.russianrocksongbook.db.util.fillDbFromJSON
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.music.MusicHelper
import jatx.russianrocksongbook.preferences.Orientation
import jatx.russianrocksongbook.preferences.Settings
import jatx.russianrocksongbook.preferences.Version
import jatx.russianrocksongbook.purchase.DonationHelper
import jatx.russianrocksongbook.view.*
import jatx.russianrocksongbook.viewmodel.CurrentScreen
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var songRepo: SongRepository
    @Inject
    lateinit var settings: Settings
    @Inject
    lateinit var mvvmViewModel: MvvmViewModel
    @Inject
    lateinit var songBookAPIAdapter: SongBookAPIAdapter
    @Inject
    lateinit var fileSystemAdapter: FileSystemAdapter
    @Inject
    lateinit var donationHelper: DonationHelper
    @Inject
    lateinit var musicHelper: MusicHelper

    private val openDirResultLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.apply {
            val pickedDir = DocumentFile.fromTreeUri(this@MainActivity, this)
            pickedDir?.apply {
                mvvmViewModel.copySongsFromDirToRepo(pickedDir)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDebug.setAppCrashHandler(songBookAPIAdapter)
        Version.init(applicationContext)

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
                asyncInit()
            }
        }
    }

    override fun onBackPressed() = mvvmViewModel.back {
        finish()
    }

    private fun asyncInit() {
        if (settings.appWasUpdated) {
            fillDbFromJSON(songRepo, applicationContext) { current, total ->
                mvvmViewModel.updateStubProgress(current, total)
            }
            deleteWrongSongs(songRepo)
            deleteWrongArtists(songRepo)
            applySongPatches(songRepo)
            mvvmViewModel.setAppWasUpdated(true)
        }
        settings.confirmAppUpdate()
        mvvmViewModel.selectScreen(CurrentScreen.SONG_LIST)
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
        try {
            if (Build.VERSION.SDK_INT < 29) {
                showFileSelectDialog()
            } else {
                openDirResultLauncher.launch(Uri.parse(DocumentsContract.EXTRA_INITIAL_URI))
            }
        } catch (e: ActivityNotFoundException) {
            showFileSelectDialog()
        }
    }

    private fun showFileSelectDialog() {
        ChooserDialog(this)
            .withFilter(true, false)
            .withStartFile(getExternalFilesDir(null)?.absolutePath)
            .withChosenListener { path, _ ->
                mvvmViewModel.copySongsFromDirToRepo(path)
            }
            .withOnCancelListener { dialog ->
                dialog.cancel()
            }
            .build()
            .show()
    }
}