package jatx.russianrocksongbook.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import dagger.hilt.android.scopes.ActivityScoped
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

@ActivityScoped
class MusicHelper @Inject constructor(
    private val activity: Activity
) {
    private val mvvmViewModel = (activity as? ComponentActivity)?.let {
        val mvvmViewModel: MvvmViewModel by it.viewModels()
        mvvmViewModel
    }

    fun openYandexMusic(searchFor: String) {
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
            mvvmViewModel?.showToast(R.string.utf8_not_supported)
        }
    }

    fun openVkMusic(searchFor: String) {
        try {
            with(activity) {
                val searchForEncoded = URLEncoder.encode(searchFor, "UTF-8")
                val uri = "https://vk.com/audio?q=$searchForEncoded"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.vkontakte.android")
                startActivity(intent)
            }
        } catch (e: UnsupportedEncodingException) {
            mvvmViewModel?.showToast(R.string.utf8_not_supported)
        } catch (e: ActivityNotFoundException) {
            mvvmViewModel?.showToast(R.string.vk_app_not_installed)
        }
    }

    fun openYoutubeMusic(searchFor: String) {
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
            mvvmViewModel?.showToast(R.string.utf8_not_supported)
        }
    }
}