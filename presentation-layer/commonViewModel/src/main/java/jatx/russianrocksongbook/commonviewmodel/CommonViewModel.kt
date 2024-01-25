package jatx.russianrocksongbook.commonviewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.commonviewmodel.contracts.MusicOpener
import jatx.russianrocksongbook.commonviewmodel.contracts.WarningSender
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.navigation.NavControllerHolder
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
    private val addWarningUseCase =
        commonViewModelDeps.addWarningUseCase

    private val _theme = MutableStateFlow(settings.theme)
    val theme = _theme.asStateFlow()

    private val _fontScaler = MutableStateFlow(settings.fontScaler)
    val fontScaler = _fontScaler.asStateFlow()

    protected open val currentMusic: Music? = null

    private val musicOpener: MusicOpener = object : MusicOpener {
        override fun openVkMusicImpl(dontAskMore: Boolean) {
            settings.vkMusicDontAsk = dontAskMore
            currentMusic?.let {
                callbacks.onOpenVkMusic(it.searchFor)
            }
        }

        override fun openYandexMusicImpl(dontAskMore: Boolean) {
            settings.yandexMusicDontAsk = dontAskMore
            currentMusic?.let {
                callbacks.onOpenYandexMusic(it.searchFor)
            }
        }

        override fun openYoutubeMusicImpl(dontAskMore: Boolean) {
            settings.youtubeMusicDontAsk = dontAskMore
            currentMusic?.let {
                callbacks.onOpenYoutubeMusic(it.searchFor)
            }
        }
    }

    protected open val currentWarnable: Warnable? = null

    private val warningSender: WarningSender = object : WarningSender {
        override fun sendWarningImpl(comment: String) {
            currentWarnable?.let {
                addWarningUseCase
                    .execute(it, comment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        when (result.status) {
                            STATUS_SUCCESS -> showToast(R.string.toast_send_warning_success)
                            STATUS_ERROR -> showToast(result.message ?: "")
                        }
                    }, { error ->
                        error.printStackTrace()
                        showToast(R.string.error_in_app)
                    })
            }
        }
    }

    val isTV = commonViewModelDeps.tvDetector.isTV

    val commonState = commonStateHolder.commonState.asStateFlow()

    private val _actions = MutableSharedFlow<UIAction>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val actions = _actions.asSharedFlow()

    private val _effects = Channel<UIEffect>()
    private val effects = _effects.receiveAsFlow()

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
        collectActions()
        collectEffects()
    }

    fun reloadSettings() {
        storage.values.forEach { viewModel ->
            viewModel._theme.update { settings.theme }
            viewModel._fontScaler.update { settings.fontScaler }
        }
    }

    private fun collectActions() {
        viewModelScope.launch {
            actions
                .onEach(::handleAction)
                .collect()
        }
    }

    private fun collectEffects() {
        viewModelScope.launch {
            effects
                .onEach(::handleEffect)
                .collect()
        }
    }

    fun submitAction(action: UIAction) {
        _actions.tryEmit(action)
    }

    fun submitEffect(effect: UIEffect) {
        _effects.trySend(effect)
    }

    protected open fun handleAction(action: UIAction) {
        when (action) {
            is Back -> back(action.fromDestinationChangedListener)
            is SelectScreen -> selectScreen(action.screenVariant)
            is AppWasUpdated -> setAppWasUpdated(action.wasUpdated)
            is OpenVkMusic -> openVkMusic(action.dontAskMore)
            is OpenYandexMusic -> openYandexMusic(action.dontAskMore)
            is OpenYoutubeMusic -> openYoutubeMusic(action.dontAskMore)
            is SendWarning -> sendWarning(action.comment)
        }
    }

    protected open fun handleEffect(effect: UIEffect) {
        when (effect) {
            is ShowToastWithText -> showToast(effect.text)
            is ShowToastWithResource -> showToast(effect.resId)
        }
    }

    protected fun back(fromDestinationChangedListener: Boolean = false) {
        with (commonState.value) {
            Log.e("back from", currentScreenVariant.toString())
            when (currentScreenVariant) {
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
                    if (fromDestinationChangedListener) {
                        if (currentArtist != ARTIST_FAVORITE) {
                            selectScreen(
                                ScreenVariant.SongList(
                                    artist = currentArtist,
                                    isBackFromSong = true
                                )
                            )
                        } else {
                            selectScreen(ScreenVariant.Favorite(isBackFromSong = true))
                        }
                    } else {
                        NavControllerHolder.navController?.popBackStack()
                    }
                }

                else -> {
                    if (currentArtist != ARTIST_FAVORITE) {
                        selectScreen(
                            ScreenVariant.SongList(
                                artist = currentArtist,
                                isBackFromSong = false
                            )
                        )
                    } else {
                        selectScreen(ScreenVariant.Favorite(isBackFromSong = false))
                    }
                }
            }
        }
    }

    protected fun selectScreen(
        screenVariant: ScreenVariant
    ) {
        commonStateHolder.commonState.update {
            it.copy(currentScreenVariant = screenVariant)
        }

        NavControllerHolder.navController
            ?.navigate(screenVariant.destination) {
                launchSingleTop = true
            }
        Log.e("navigate", screenVariant.destination)
    }

    protected fun setAppWasUpdated(wasUpdated: Boolean) {
        commonStateHolder.commonState.update {
            it.copy(appWasUpdated = wasUpdated)
        }
    }

    protected fun showToast(toastText: String) = toasts.showToast(toastText)

    protected fun showToast(@StringRes resId: Int) = toasts.showToast(resId)

    private fun doNothing() = Unit

    private fun openVkMusic(dontAskMore: Boolean) =
        musicOpener.openVkMusicImpl(dontAskMore)

    private fun openYandexMusic(dontAskMore: Boolean) =
        musicOpener.openYandexMusicImpl(dontAskMore)

    private fun openYoutubeMusic(dontAskMore: Boolean) =
        musicOpener.openYoutubeMusicImpl(dontAskMore)

    private fun sendWarning(comment: String) =
        warningSender.sendWarningImpl(comment)
}