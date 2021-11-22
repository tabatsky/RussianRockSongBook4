package jatx.russianrocksongbook.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.model.data.*
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.interfaces.Cloud
import jatx.russianrocksongbook.viewmodel.interfaces.Local
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
open class MvvmViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val screenStateHolder: ScreenStateHolder
): ViewModel() {
    val songBookAPIAdapter = viewModelParam.songBookAPIAdapter
    val settings = viewModelParam.settings
    val callbacks = viewModelParam.callbacks
    val context = viewModelParam.context
    val songRepo = viewModelParam.songRepo
    val userInfo = viewModelParam.userInfo
    val fileSystemAdapter = viewModelParam.fileSystemAdapter

    val currentScreenVariant = screenStateHolder
        .currentScreenVariant
        .asStateFlow()

    val currentArtist = screenStateHolder
        .currentArtist
        .asStateFlow()

    val artistList = screenStateHolder
        .artistList
        .asStateFlow()

    val appWasUpdated = screenStateHolder
        .appWasUpdated
        .asStateFlow()

    private var getArtistsDisposable: Disposable? = null

    fun back(onFinish: () -> Unit = {}) {
        Log.e("current screen", currentScreenVariant.value.toString())
        when (currentScreenVariant.value) {
            CurrentScreenVariant.START, CurrentScreenVariant.SONG_LIST -> {
                onFinish()
            }
            CurrentScreenVariant.CLOUD_SONG_TEXT -> {
                selectScreen(CurrentScreenVariant.CLOUD_SEARCH, true)
            }
            CurrentScreenVariant.SONG_TEXT -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(CurrentScreenVariant.SONG_LIST)
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE)
                }
            }
            else -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(CurrentScreenVariant.SONG_LIST, false)
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE, false)
                }
            }
        }
    }

    fun selectScreen(
        screen: CurrentScreenVariant,
        isBackFromSong: Boolean = false
    ) {
        screenStateHolder.currentScreenVariant.value = screen
        Log.e("select screen", currentScreenVariant.value.toString())
        if (screen == CurrentScreenVariant.SONG_LIST && !isBackFromSong) {
            getArtistsDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            getArtistsDisposable = songRepo
                .getArtists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    screenStateHolder.artistList.value = it
                }
            callbacks.onArtistSelected(currentArtist.value)
        }
        if (screen == CurrentScreenVariant.FAVORITE) {
            getArtistsDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            getArtistsDisposable = songRepo
                .getArtists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    screenStateHolder.artistList.value = it
                }
            callbacks.onArtistSelected(ARTIST_FAVORITE)
        }
        if (screen == CurrentScreenVariant.CLOUD_SEARCH && !isBackFromSong) {
            callbacks.onCloudSearchScreenSelected()
        }
    }

    fun setAppWasUpdated(value: Boolean) {
        screenStateHolder.appWasUpdated.value = value
    }

    fun purchaseItem(sku: String) {
        callbacks.onPurchaseItem(sku)
    }

    fun showToast(toastText: String) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }

    fun showToast(@StringRes resId: Int) {
        val toastText = context.getString(resId)
        showToast(toastText)
    }

    fun openYandexMusic(dontAskMore: Boolean) {
        when (this) {
            is Local -> openYandexMusicLocal(dontAskMore)
            is Cloud -> openYandexMusicCloud(dontAskMore)
        }
    }

    fun openVkMusic(dontAskMore: Boolean) {
        when (this) {
            is Local -> openVkMusicLocal(dontAskMore)
            is Cloud -> openVkMusicCloud(dontAskMore)
        }
    }

    fun openYoutubeMusic(dontAskMore: Boolean) {
        when (this) {
            is Local -> openYoutubeMusicLocal(dontAskMore)
            is Cloud -> openYoutubeMusicCloud(dontAskMore)
        }
    }

    fun sendWarning(comment: String) {
        when (this) {
            is Local -> sendWarningLocal(comment)
            is Cloud -> sendWarningCloud(comment)
        }
    }
}