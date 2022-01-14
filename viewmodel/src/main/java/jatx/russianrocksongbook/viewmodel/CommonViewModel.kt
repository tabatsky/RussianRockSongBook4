package jatx.russianrocksongbook.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import jatx.russianrocksongbook.domain.repository.ARTIST_FAVORITE
import jatx.russianrocksongbook.viewmodel.contracts.SongTextViewModelContract
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
open class CommonViewModel @Inject constructor(
    private val commonStateHolder: CommonStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): ViewModel() {
    val settings =
        commonViewModelDeps.settingsRepository
    val callbacks =
        commonViewModelDeps.callbacks
    val resources =
        commonViewModelDeps.resources
    private val toasts =
        commonViewModelDeps.toasts

    private val getArtistsUseCase = commonViewModelDeps.getArtistsUseCase

    val currentScreenVariant = commonStateHolder
        .currentScreenVariant
        .asStateFlow()

    val currentArtist = commonStateHolder
        .currentArtist
        .asStateFlow()

    val artistList = commonStateHolder
        .artistList
        .asStateFlow()

    val appWasUpdated = commonStateHolder
        .appWasUpdated
        .asStateFlow()

    private var getArtistsDisposable: Disposable? = null

    fun back(onFinish: () -> Unit = {}) {
        Log.e("current screen", currentScreenVariant.value.toString())
        when (currentScreenVariant.value) {
            CurrentScreenVariant.START,
            CurrentScreenVariant.SONG_LIST,
            CurrentScreenVariant.FAVORITE -> {
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
        commonStateHolder.currentScreenVariant.value = screen
        Log.e("select screen", currentScreenVariant.value.toString())
        if (screen == CurrentScreenVariant.SONG_LIST && !isBackFromSong) {
            getArtistsDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            getArtistsDisposable = getArtistsUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    commonStateHolder.artistList.value = it
                }
            callbacks.onArtistSelected(currentArtist.value)
        }
        if (screen == CurrentScreenVariant.FAVORITE) {
            getArtistsDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            getArtistsDisposable = getArtistsUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    commonStateHolder.artistList.value = it
                }
            callbacks.onArtistSelected(ARTIST_FAVORITE)
        }
        if (screen == CurrentScreenVariant.CLOUD_SEARCH && !isBackFromSong) {
            callbacks.onCloudSearchScreenSelected()
        }
    }

    fun setAppWasUpdated(value: Boolean) {
        commonStateHolder.appWasUpdated.value = value
    }

    fun showToast(toastText: String) = toasts.showToast(toastText)

    fun showToast(@StringRes resId: Int) = toasts.showToast(resId)

    fun openYandexMusic(dontAskMore: Boolean) {
        if (this is SongTextViewModelContract) {
            openYandexMusicImpl(dontAskMore)
        }
    }

    fun openVkMusic(dontAskMore: Boolean) {
        if (this is SongTextViewModelContract) {
            openVkMusicImpl(dontAskMore)
        }
    }

    fun openYoutubeMusic(dontAskMore: Boolean) {
        if (this is SongTextViewModelContract) {
            openYoutubeMusicImpl(dontAskMore)
        }
    }

    fun sendWarning(comment: String) {
        if (this is SongTextViewModelContract) {
            sendWarningImpl(comment)
        }
    }
}