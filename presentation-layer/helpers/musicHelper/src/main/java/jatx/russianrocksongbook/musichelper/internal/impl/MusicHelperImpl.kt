package jatx.russianrocksongbook.musichelper.internal.impl

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.musichelper.api.MusicHelper
import jatx.russianrocksongbook.musichelper.R
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

@ActivityScoped
@BoundTo(supertype = MusicHelper::class, component = ActivityComponent::class)
internal class MusicHelperImpl @Inject constructor(
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
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW, Uri.parse(uri)),
                        getString(R.string.search_at_yandex_music)
                    )
                )
            }
        } catch (e: UnsupportedEncodingException) {
            commonViewModel?.showToast(R.string.utf8_not_supported)
        }
    }

    override fun openVkMusic(searchFor: String) {
        try {
            with(activity) {
                val searchForEncoded = URLEncoder.encode(searchFor, "UTF-8")
                val uriApp = "https://vk.com/audio?q=$searchForEncoded"
                val uriBrowser = "https://m.vk.com/audio?q=$searchForEncoded"
                val intentList = arrayListOf<Intent>()
                listOf(
                    "com.android.chrome",
                    "com.yandex.browser",
                    "com.opera.browser",
                    "org.mozilla.firefox"
                ).forEach {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriBrowser))
                    intent.setPackage(it)
                    intentList.add(intent)
                }
                listOf(
                    "com.uma.musicvk",
                    "com.vkontakte.android"
                ).forEach {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriApp))
                    intent.setPackage(it)
                    intentList.add(intent)
                }

                val chooserIntent = Intent
                    .createChooser(
                        intentList.removeAt(intentList.size - 1),
                        getString(R.string.vk_music_chooser)
                    )
                chooserIntent.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toTypedArray()
                )
                startActivity(chooserIntent)
            }
        } catch (e: UnsupportedEncodingException) {
            commonViewModel?.showToast(R.string.utf8_not_supported)
        } catch (e: ActivityNotFoundException) {
            commonViewModel?.showToast(R.string.vk_app_not_installed)
        }
    }

    override fun openYoutubeMusic(searchFor: String) {
        try {
            with(activity) {
                val searchForEncoded = URLEncoder.encode(searchFor.replace(" ", "+"), "UTF-8")
                val uri = "https://music.youtube.com/search?q=$searchForEncoded"
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW, Uri.parse(uri)),
                        getString(R.string.search_at_youtube_music)
                    )
                )
            }
        } catch (e: UnsupportedEncodingException) {
            commonViewModel?.showToast(R.string.utf8_not_supported)
        }
    }
}