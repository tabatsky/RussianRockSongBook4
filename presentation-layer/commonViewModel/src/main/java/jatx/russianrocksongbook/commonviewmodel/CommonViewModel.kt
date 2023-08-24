package jatx.russianrocksongbook.commonviewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.commonviewmodel.contracts.SongTextViewModelContract
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.navigation.NavControllerHolder
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

    val commonState = commonStateHolder.commonState.asStateFlow()

    private val actionFlow = MutableSharedFlow<UIAction>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

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

    init {
        Log.e("viewModel", "init")
        collectActions()
    }

    private fun collectActions() {
        viewModelScope.launch {
            actionFlow
                .onEach(::handleAction)
                .collect()
        }
    }

    fun submitAction(action: UIAction) {
        actionFlow.tryEmit(action)
    }

    protected open fun handleAction(action: UIAction) {
        Log.e("action", action.toString())
        when (action) {
            is Back -> back()
            is SelectScreen -> selectScreen(action.screenVariant)
            is AppWasUpdated -> setAppWasUpdated(action.wasUpdated)
        }
    }

    protected fun back() {
        Log.e("back from", currentScreenVariant.value.toString())
        when (currentScreenVariant.value) {
            is ScreenVariant.Start -> {
                doNothing()
            }
            is ScreenVariant.SongList,
            is ScreenVariant.Favorite -> {
                callbacks.onFinish()
            }
            is ScreenVariant.CloudSongText -> {
                selectScreen(ScreenVariant.CloudSearch(isBackFromSong = true))
            }
            is ScreenVariant.SongText -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(
                        ScreenVariant.SongList(
                            artist = currentArtist.value,
                            isBackFromSong = true))
                } else {
                    selectScreen(ScreenVariant.Favorite(isBackFromSong = true))
                }
            }
            else -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(
                        ScreenVariant.SongList(
                            artist = currentArtist.value,
                            isBackFromSong = false))
                } else {
                    selectScreen(ScreenVariant.Favorite(isBackFromSong = false))
                }
            }
        }
    }

    protected fun selectScreen(
        screenVariant: ScreenVariant
    ) {
        commonStateHolder.commonState.value =
            commonState.value.copy(currentScreenVariant = screenVariant)
        commonStateHolder.currentScreenVariant.value = screenVariant
        NavControllerHolder.navController.navigate(screenVariant.destination)
        Log.e("navigate", screenVariant.destination)
    }

    protected fun setAppWasUpdated(value: Boolean) {
        commonStateHolder.commonState.value =
            commonState.value.copy(appWasUpdated = value)
        commonStateHolder.appWasUpdated.value = value
    }


    private fun doNothing() = Unit

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