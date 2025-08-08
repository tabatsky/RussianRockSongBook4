package jatx.russianrocksongbook.commonviewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonappstate.AppState
import jatx.russianrocksongbook.commonviewmodel.contracts.MusicOpener
import jatx.russianrocksongbook.commonviewmodel.contracts.WarningSender
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.navigation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
open class CommonViewModel @Inject constructor(
    private val appStateHolder: AppStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): ViewModel() {
    companion object {
        var needReset = false

        private const val key = "Common"

        val storage = ConcurrentHashMap<String, CommonViewModel>()

        private var _appNavigator: AppNavigator? = null
        val appNavigator: AppNavigator
            get() = _appNavigator!!

        @Composable
        fun getInstance(): CommonViewModel {
            if (!storage.containsKey(key)) {
                storage[key] = hiltViewModel()
            }
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as CommonViewModel
        }

        fun getStoredInstance() = storage[key]

        fun clearStorage() = storage.clear()

        fun clearAppNavigator() {
            _appNavigator = null
        }
    }

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
    private val addSongToCloudUseCase =
        commonViewModelDeps.addSongToCloudUseCase

    private var collectActionsJob: Job? = null
    private var collectEffectsJob: Job? = null

    private var uploadSongJob: Job? = null

    private val _theme = MutableStateFlow(settings.theme)
    val theme = _theme.asStateFlow()

    private val _fontScaler = MutableStateFlow(settings.fontScaler)
    val fontScaler = _fontScaler.asStateFlow()

    val isTV = commonViewModelDeps.tvDetector.isTV

    val appStateFlow = appStateHolder.appStateFlow

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
            currentWarnable?.let { warnable ->
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        try {
                            val result = withContext(Dispatchers.IO) {
                                addWarningUseCase
                                    .execute(warnable, comment)
                            }
                            when (result.status) {
                                STATUS_SUCCESS -> showToast(R.string.toast_send_warning_success)
                                STATUS_ERROR -> showToast(result.message ?: "")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showToast(R.string.error_in_app)
                        }
                    }
                }
            }
        }
    }

    private val _actions = MutableSharedFlow<UIAction>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val actions = _actions.asSharedFlow()

    private val _effects = Channel<UIEffect>()
    private val effects = _effects.receiveAsFlow()

    open fun resetState() = appStateHolder.reset()

    fun injectBackStack(backStack: NavBackStack) {
        if (_appNavigator == null) {
            _appNavigator = AppNavigator().also {
                it.injectCallbacks(
                    getAppState = {
                        getStoredInstance()?.appStateFlow?.value
                            ?: throw IllegalStateException("cannot resolve current app state")
                    },
                    onChangeCurrentScreenVariant = ::changeCurrentScreenVariant
                )
            }
        }
        appNavigator.updateBackStack(backStack)
    }

    fun submitAction(action: UIAction) {
        _actions.tryEmit(action)
    }

    fun submitEffect(effect: UIEffect) {
        _effects.trySend(effect)
    }

    protected open fun handleAction(action: UIAction) {
        when (action) {
            is Back -> back()
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

    fun launchJobsIfNecessary() {
        if (collectActionsJob?.isActive != true) {
            collectActionsJob = collectActions()
        }
        if (collectEffectsJob?.isActive != true) {
            collectEffectsJob = collectEffects()
        }
    }

    private fun collectActions() = viewModelScope.launch {
        actions
            .onEach(::handleAction)
            .collect()
    }

    private fun collectEffects() = viewModelScope.launch {
        effects
            .onEach(::handleEffect)
            .collect()
    }

    fun reloadSettings() {
        storage.values.forEach { viewModel ->
            viewModel._theme.update { settings.theme }
            viewModel._fontScaler.update { settings.fontScaler }
        }
    }

    protected fun back() {
        Log.e("back by","user")
        when (appNavigator.currentScreenVariant) {
            is SongListScreenVariant,
            is FavoriteScreenVariant,
            is StartScreenVariant -> {
                needReset = true
                callbacks.onFinish()
            }

            else -> appNavigator.backByUser()
        }
    }

    protected fun selectScreen(newScreenVariant: ScreenVariant) =
        appNavigator.selectScreen(newScreenVariant)

    fun changeCurrentScreenVariant(screenVariant: ScreenVariant) {
        val appState = appStateFlow.value
        val newState = when (screenVariant) {
            is CloudSearchScreenVariant -> {
                appState.copy(
                    lastRandomKey = screenVariant.randomKey
                )
            }

            is TextSearchListScreenVariant -> {
                appState.copy(
                    lastRandomKey = screenVariant.randomKey
                )
            }

            else -> appState
        }
        changeAppState(newState)
    }

    protected fun setAppWasUpdated(wasUpdated: Boolean) {
        val appState = appStateFlow.value
        val newState = appState.copy(appWasUpdated = wasUpdated)
        changeAppState(newState)
    }

    protected fun changeAppState(appState: AppState) = appStateHolder.changeAppState(appState)

    protected fun showToast(toastText: String) = toasts.showToast(toastText)

    protected fun showToast(@StringRes resId: Int) = toasts.showToast(resId)

    protected fun uploadSongToCloud(song: Song, setUploadButtonEnabled: (Boolean) -> Unit) {
        setUploadButtonEnabled(false)
        uploadSongJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        uploadSongJob = viewModelScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        addSongToCloudUseCase.execute(song)
                    }
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.toast_upload_to_cloud_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                    setUploadButtonEnabled(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(R.string.error_in_app)
                    setUploadButtonEnabled(true)
                }
            }
        }
    }

    private fun openVkMusic(dontAskMore: Boolean) =
        musicOpener.openVkMusicImpl(dontAskMore)

    private fun openYandexMusic(dontAskMore: Boolean) =
        musicOpener.openYandexMusicImpl(dontAskMore)

    private fun openYoutubeMusic(dontAskMore: Boolean) =
        musicOpener.openYoutubeMusicImpl(dontAskMore)

    private fun sendWarning(comment: String) =
        warningSender.sendWarningImpl(comment)
}