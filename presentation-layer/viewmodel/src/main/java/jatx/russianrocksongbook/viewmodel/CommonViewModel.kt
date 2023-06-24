package jatx.russianrocksongbook.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.viewmodel.contracts.SongTextViewModelContract
import jatx.russianrocksongbook.navigation.CurrentScreenVariant
import jatx.russianrocksongbook.navigation.NavControllerHolder
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
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

    val currentScreenVariant = commonStateHolder
        .currentScreenVariant
        .asStateFlow()

    val currentArtist = commonStateHolder
        .currentArtist
        .asStateFlow()

    val appWasUpdated = commonStateHolder
        .appWasUpdated
        .asStateFlow()

    val artistList = commonStateHolder
        .artistList
        .asStateFlow()


    companion object {
        private const val key = "Common"

        val storage = ConcurrentHashMap<String, CommonViewModel>()

        @Composable
        fun getInstance(): CommonViewModel {
            if (!storage.containsKey(key)) storage[key] = hiltViewModel()
            return storage[key] as CommonViewModel
        }

        fun getStoredInstance() = storage[key]

        fun clearStorage() = storage.clear()
    }

    fun back(onFinish: () -> Unit = {}) {
        Log.e("back from", currentScreenVariant.value.toString())
        when (currentScreenVariant.value) {
            is CurrentScreenVariant.START,
            is CurrentScreenVariant.SONG_LIST,
            is CurrentScreenVariant.FAVORITE -> {
                onFinish()
            }
            is CurrentScreenVariant.CLOUD_SONG_TEXT -> {
                selectScreen(CurrentScreenVariant.CLOUD_SEARCH(isBackFromSong = true))
            }
            is CurrentScreenVariant.SONG_TEXT -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(
                        CurrentScreenVariant.SONG_LIST(
                        artist = currentArtist.value,
                        isBackFromSong = true))
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE(isBackFromSong = true))
                }
            }
            else -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(
                        CurrentScreenVariant.SONG_LIST(
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
        NavControllerHolder.navController.navigate(screen.destination)
        Log.e("navigate", screen.destination)
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
}