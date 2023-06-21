package jatx.russianrocksongbook.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.viewmodel.contracts.SongTextViewModelContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    val isTV = commonViewModelDeps.tvDetector.isTV

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

    private var getArtistsJob: Job? = null

    fun back(onFinish: () -> Unit = {}) {
        Log.e("current screen", currentScreenVariant.value.toString())
        when (currentScreenVariant.value) {
            is CurrentScreenVariant.START,
            is CurrentScreenVariant.SONG_LIST,
            is CurrentScreenVariant.FAVORITE -> {
                onFinish()
            }
            CurrentScreenVariant.CLOUD_SONG_TEXT -> {
                selectScreen(CurrentScreenVariant.CLOUD_SEARCH(isBackFromSong = true))
            }
            CurrentScreenVariant.SONG_TEXT -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(CurrentScreenVariant.SONG_LIST(
                        artist = currentArtist.value,
                        isBackFromSong = true))
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE(isBackFromSong = true))
                }
            }
            else -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(CurrentScreenVariant.SONG_LIST(
                        artist = currentArtist.value,
                        isBackFromSong = false))
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE(isBackFromSong = false))
                }
            }
        }
    }

    fun selectScreen(
        screen: CurrentScreenVariant
    ) {
        commonStateHolder.currentScreenVariant.value = screen
        Log.e("select screen", currentScreenVariant.value.toString())
    }

    fun setAppWasUpdated(value: Boolean) {
        commonStateHolder.appWasUpdated.value = value
    }

    fun showToast(toastText: String) = toasts.showToast(toastText)

    fun showToast(@StringRes resId: Int) = toasts.showToast(resId)

    fun openVkMusic(dontAskMore: Boolean) =
        (this as? SongTextViewModelContract)?.openVkMusicImpl(dontAskMore)

    fun openYandexMusic(dontAskMore: Boolean) =
        (this as? SongTextViewModelContract)?.openYandexMusicImpl(dontAskMore)

    fun openYoutubeMusic(dontAskMore: Boolean) =
        (this as? SongTextViewModelContract)?.openYoutubeMusicImpl(dontAskMore)

    fun sendWarning(comment: String) =
        (this as? SongTextViewModelContract)?.sendWarningImpl(comment)

    fun updateArtists() {
        getArtistsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        getArtistsJob = viewModelScope.launch {
            getArtistsUseCase
                .execute()
                .collect {
                    withContext(Dispatchers.Main) {
                        commonStateHolder.artistList.value = it
                    }
                }
        }
    }
}