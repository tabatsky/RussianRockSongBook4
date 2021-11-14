package jatx.russianrocksongbook

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.documentfile.provider.DocumentFile
import com.android.billingclient.api.*
import com.obsez.android.lib.filechooser.ChooserDialog
import dagger.hilt.android.AndroidEntryPoint
import jatx.russianrocksongbook.data.*
import jatx.russianrocksongbook.db.deleteWrongArtists
import jatx.russianrocksongbook.db.deleteWrongSongs
import jatx.russianrocksongbook.db.util.applySongPatches
import jatx.russianrocksongbook.db.util.fillDbFromJSON
import jatx.russianrocksongbook.debug.AppDebug
import jatx.russianrocksongbook.preferences.Orientation
import jatx.russianrocksongbook.preferences.Settings
import jatx.russianrocksongbook.preferences.Version
import jatx.russianrocksongbook.view.*
import jatx.russianrocksongbook.viewmodel.CurrentScreen
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.SKUS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PurchasesUpdatedListener {
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

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDebug.setAppCrashHandler(songBookAPIAdapter)
        Version.init(applicationContext)
        registerForResult()

        when (settings.orientation) {
            Orientation.PORTRAIT -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

        mvvmViewModel.onRestartApp = {
            val packageManager: PackageManager = packageManager
            val intent = packageManager.getLaunchIntentForPackage(getPackageName())
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }

        mvvmViewModel.onReviewApp = {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=jatx.russianrocksongbook")
                )
            )
        }

        mvvmViewModel.onShowDevSite = {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://tabatsky.ru"
                    )
                )
            )
        }

        mvvmViewModel.onOpenVkMusic = { searchFor ->
            try {
                val searchForEncoded = URLEncoder.encode(searchFor, "UTF-8")
                val uri = "https://vk.com/audio?q=$searchForEncoded"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.vkontakte.android")
                startActivity(intent)
            } catch (e: UnsupportedEncodingException) {
                mvvmViewModel.showToast(R.string.utf8_not_supported)
            } catch (e: ActivityNotFoundException) {
                mvvmViewModel.showToast(R.string.vk_app_not_installed)
            }
        }

        mvvmViewModel.onOpenYoutubeMusic = { searchFor ->
            try {
                val searchForEncoded = URLEncoder.encode(searchFor.replace(" ", "+"), "UTF-8")
                val uri = "https://music.youtube.com/search?q=$searchForEncoded"
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW, Uri.parse(uri)),
                        getString(R.string.search_at_google_play)
                    )
                )
            } catch (e: UnsupportedEncodingException) {
                mvvmViewModel.showToast(R.string.utf8_not_supported)
            }
        }

        mvvmViewModel

        mvvmViewModel.onAddSongsFromDir = {
            addSongsFromDir()
        }

        mvvmViewModel.onPurchaseItem = {
            purchaseItem(it)
        }

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
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
        }

        billingClient = BillingClient
            .newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    checkDonations()
                } else {
                    Log.e("response code", billingResult.responseCode.toString())
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.e("billing service", "disconnected")
            }
        })

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "russianrocksongbook:song-text-power")
        wakeLock.acquire()
    }

    override fun onDestroy() {
        wakeLock.release()
        super.onDestroy()
    }

    override fun onBackPressed() = mvvmViewModel.back {
        finish()
    }

    private fun registerForResult() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val treeUri = data?.data
                treeUri?.apply {
                    val pickedDir = DocumentFile.fromTreeUri(this@MainActivity, this)
                    pickedDir?.apply {
                        mvvmViewModel.copySongsFromDirToRepo(pickedDir)
                    }
                }
            }
        }
    }

    private fun addSongsFromDir() {
        try {
            if (Build.VERSION.SDK_INT < 29) {
                showFileSelectDialog()
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                resultLauncher.launch(intent)
            }
        } catch (e: ActivityNotFoundException) {
            showFileSelectDialog()
        }
    }

    private fun showFileSelectDialog() {
        ChooserDialog(this)
            .withFilter(true, false)
            .withStartFile(Environment.getExternalStorageDirectory().absolutePath)
            .withChosenListener { path, _ ->
                mvvmViewModel.copySongsFromDirToRepo(path)
            }
            .withOnCancelListener { dialog ->
                dialog.cancel()
            }
            .build()
            .show()
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        mutableList: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && mutableList != null) {
            Log.e("mutableList0", mutableList.toString())
            mutableList.forEach {
                consumePurchase(it)
            }
        }
    }

    private fun checkDonations() {
        val purchasesResult = billingClient.queryPurchases("inapp")

        purchasesResult.purchasesList?.forEach {
            consumePurchase(it)
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        for (sku in purchase.skus) {
            if (sku in SKUS) {
                val consumeParams = ConsumeParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams) { responseCode, _ ->
                    Log.e("consume", responseCode.responseCode.toString())
                    mvvmViewModel.showToast(R.string.thanks_for_donation)
                }
            }
        }
    }

    private fun purchaseItem(sku: String) {
        val skuList = ArrayList<String>()
        skuList.add(sku)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, mutableList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && mutableList != null) {
                Log.e("mutableList", mutableList.toString())
                mutableList.forEach {
                    if (it.sku == sku) {
                        Log.e("billing flow", "launching")
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(it)
                            .build()
                        billingClient.launchBillingFlow(this@MainActivity, flowParams)
                    }
                }
            }
        }
    }
}