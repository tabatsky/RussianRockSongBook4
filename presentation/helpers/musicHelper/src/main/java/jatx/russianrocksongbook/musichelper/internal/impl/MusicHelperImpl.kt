package jatx.russianrocksongbook.musichelper.internal.impl

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.musichelper.api.MusicHelper
import jatx.russianrocksongbook.musichelper.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

@ActivityScoped
@BoundTo(supertype = MusicHelper::class, component = ActivityComponent::class)
class MusicHelperImpl @Inject constructor(
    private val activity: Activity
): MusicHelper {
    private val commonViewModel by lazy {
        CommonViewModel.getStoredInstance()
    }

    override fun openYandexMusic(searchFor: String) {
        try {
            with(activity) {
                val searchForEncoded = URLEncoder.encode(searchFor.replace(" ", "+"), "UTF-8")
                val uri = "https://music.yandex.ru/search?text=$searchForEncoded"
                showChooser(uri)
            }
        } catch (e: UnsupportedEncodingException) {
            commonViewModel?.submitEffect(
                ShowToastWithResource(R.string.utf8_not_supported)
            )
        }
    }

    override fun openVkMusic(searchFor: String) {
        try {
            with(activity) {
                val searchForEncoded = URLEncoder.encode(searchFor, "UTF-8")
                //val uriApp = "https://vk.com/audio?q=$searchForEncoded"
                val uriBrowser = "https://m.vk.com/audio?q=$searchForEncoded"
                showChooser(uriBrowser)
            }
        } catch (e: UnsupportedEncodingException) {
            commonViewModel?.submitEffect(
                ShowToastWithResource(R.string.utf8_not_supported)
            )
        } catch (e: ActivityNotFoundException) {
            commonViewModel?.submitEffect(
                ShowToastWithResource(R.string.vk_app_not_installed)
            )
        }
    }

    override fun openYoutubeMusic(searchFor: String) {
        try {
            with(activity) {
                val searchForEncoded = URLEncoder.encode(searchFor.replace(" ", "+"), "UTF-8")
                val uri = "https://music.youtube.com/search?q=$searchForEncoded"
                showChooser(uri)
            }
        } catch (e: UnsupportedEncodingException) {
            commonViewModel?.submitEffect(
                ShowToastWithResource(R.string.utf8_not_supported)
            )
        }
    }
}


private fun Activity.showChooser(uri: String) {
    val intentList = makeIntentList(uri) // + makeIntentList(uriApp)

    val chooserIntent = Intent
        .createChooser(
            intentList.last(),
            getString(R.string.chooser_text)
        )
    chooserIntent.putExtra(
        Intent.EXTRA_INITIAL_INTENTS,
        intentList.dropLast(1).toTypedArray()
    )
    startActivity(chooserIntent)
}

@SuppressLint("QueryPermissionsNeeded")
private fun Activity.makeIntentList(uri: String): List<Intent> {
    val origIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    val infoList = if (android.os.Build.VERSION.SDK_INT >= 23){
        packageManager.queryIntentActivities(origIntent, PackageManager.MATCH_ALL)
    } else{
        packageManager.queryIntentActivities(origIntent, 0)
    }
    return infoList.map {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage(it.activityInfo.packageName)
        intent
    }
}
