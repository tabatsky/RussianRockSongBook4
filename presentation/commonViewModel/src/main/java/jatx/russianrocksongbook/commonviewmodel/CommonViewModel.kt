package jatx.russianrocksongbook.commonviewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.contracts.MusicOpener
import jatx.russianrocksongbook.commonviewmodel.contracts.WarningSender
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
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
        _appNavigator = AppNavigator(backStack).also {
            it.inject { submitAction(Back(true)) }
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
            is Back -> back(action.byDestinationChangedListener)
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

    protected fun back(byDestinationChangedListener: Boolean = false) {
        Log.e("back by",
            if (byDestinationChangedListener) "destination listener" else "user")
        if (byDestinationChangedListener) {
            backByDestinationChangedListener()
        } else {
            backByUserPressBackButton()
        }
    }

    private fun backByDestinationChangedListener() {
        with (appStateFlow.value) {
            Log.e("back from", currentScreenVariant.toString())
            when (currentScreenVariant) {
                is StartScreenVariant -> {
                    doNothing()
                }

                is SongListScreenVariant -> {
                    doNothing()
                }

                is SongTextScreenVariant  -> {
                    if (currentArtist != ARTIST_FAVORITE) {
                        selectScreen(
                            SongListScreenVariant(
                                artist = currentArtist,
                                isBackFromSomeScreen = true
                            )
                        )
                    } else {
                        selectScreen(FavoriteScreenVariant(isBackFromSomeScreen = true))
                    }
                }

                is SongTextByArtistAndTitleScreenVariant -> {
                    if (previousScreenVariant is FavoriteScreenVariant) {
                        submitAction(
                            ShowSongs(
                                artist = currentScreenVariant.artist,
                                songTitleToPass = currentScreenVariant.title
                            )
                        )
                    }
                }

                is CloudSongTextScreenVariant -> {
                    selectScreen(
                        CloudSearchScreenVariant(
                            randomKey = lastRandomKey,
                            isBackFromSong = true
                        ))
                }

                is TextSearchSongTextScreenVariant -> {
                    selectScreen(
                        TextSearchListScreenVariant(
                            randomKey = lastRandomKey,
                            isBackFromSong = true
                        ))
                }

                else -> {
                    if (currentArtist != ARTIST_FAVORITE) {
                        selectScreen(
                            SongListScreenVariant(
                                artist = currentArtist,
                                isBackFromSomeScreen = true
                            )
                        )
                    } else {
                        selectScreen(FavoriteScreenVariant(isBackFromSomeScreen = true))
                    }
                }
            }
        }
    }

    private fun backByUserPressBackButton() {
        when (appStateFlow.value.currentScreenVariant) {
            is SongListScreenVariant,
            is FavoriteScreenVariant,
            is StartScreenVariant -> {
                needReset = true
                callbacks.onFinish()
            }

            else -> appNavigator.pop()
        }
    }

    protected fun selectScreen(
        newScreenVariant: ScreenVariant
    ) {
        val currentScreenVariant = appStateFlow
            .value
            .currentScreenVariant

        val isSongByArtistAndTitle = currentScreenVariant is SongTextByArtistAndTitleScreenVariant
        val isStart = currentScreenVariant is StartScreenVariant
        val isFavorite = currentScreenVariant is FavoriteScreenVariant
        val becomeSongList = newScreenVariant is SongListScreenVariant
        val isSongList = currentScreenVariant is SongListScreenVariant
        val becomeFavorite = newScreenVariant is FavoriteScreenVariant
        val becomeSongByArtistAndTitle = newScreenVariant is SongTextByArtistAndTitleScreenVariant

        val isAddSong = currentScreenVariant is AddSongScreenVariant
        val isAddArtist = currentScreenVariant is AddArtistScreenVariant

        val isSongText = currentScreenVariant is SongTextScreenVariant
        val becomeSongText = newScreenVariant is SongTextScreenVariant
        val isCloudSongText = currentScreenVariant is CloudSongTextScreenVariant
        val becomeCloudSongText = newScreenVariant is CloudSongTextScreenVariant
        val isTextSearchSongText = currentScreenVariant is TextSearchSongTextScreenVariant
        val becomeTextSearchSongText = newScreenVariant is TextSearchSongTextScreenVariant

        val artistNow = (currentScreenVariant as? SongListScreenVariant)?.artist
        val artistBecome = (newScreenVariant as? SongListScreenVariant)?.artist

        val isBackNow = (currentScreenVariant as? SongListScreenVariant)?.isBackFromSomeScreen
        val isBackBecome = (newScreenVariant as? SongListScreenVariant)?.isBackFromSomeScreen

        val needToPopTwice = isSongByArtistAndTitle || isAddArtist && becomeSongList
        val needToReturn = isSongList && becomeSongList &&
                (artistNow == artistBecome) &&
                (isBackNow == isBackBecome)

        var needToPop = false
        needToPop = needToPop || isStart && becomeSongList
        needToPop = needToPop || isFavorite && becomeSongList
        needToPop = needToPop || isSongList && becomeFavorite
        needToPop = needToPop || isAddSong && becomeSongByArtistAndTitle

        var needToPopWithSkippingBackOnce = false
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || (isSongList || isFavorite) && (becomeSongList || becomeFavorite)
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || isSongText && becomeSongText
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || isCloudSongText && becomeCloudSongText
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || isTextSearchSongText && becomeTextSearchSongText

        if (needToPopTwice) {
            appNavigator.pop(dontSubmitBackAction = true, times = 2)
        } else if (needToPop) {
            appNavigator.pop(dontSubmitBackAction = true)
        } else if (needToReturn) {
            return
        } else if (needToPopWithSkippingBackOnce) {
            appNavigator.pop(skipOnce = true)
        }

        changeCurrentScreenVariant(newScreenVariant)

        val isBackFromCertainScreen = (newScreenVariant as? SongListScreenVariant)?.isBackFromSomeScreen
            ?: (newScreenVariant as? FavoriteScreenVariant)?.isBackFromSomeScreen
            ?: (newScreenVariant as? CloudSearchScreenVariant)?.isBackFromSong
            ?: (newScreenVariant as? TextSearchListScreenVariant)?.isBackFromSong
            ?: false
                && !isAddArtist // already popped
                && (appNavigator.backStack.lastOrNull()?.javaClass == newScreenVariant.javaClass)

        if (!isBackFromCertainScreen) {
            appNavigator.push(newScreenVariant)
        } else {
            appNavigator.replace(newScreenVariant)
        }

        Log.e("navigated", newScreenVariant.toString())
    }

    fun changeCurrentScreenVariant(screenVariant: ScreenVariant) {
        val appState = appStateFlow.value
        val previousScreenVariant = appState.currentScreenVariant
        val newState = if (screenVariant is CloudSearchScreenVariant) {
            appState.copy(
                currentScreenVariant = screenVariant,
                previousScreenVariant = previousScreenVariant,
                lastRandomKey = screenVariant.randomKey
            )
        } else if (screenVariant is TextSearchListScreenVariant) {
            appState.copy(
                currentScreenVariant = screenVariant,
                previousScreenVariant = previousScreenVariant,
                lastRandomKey = screenVariant.randomKey
            )
        } else {
            appState.copy(
                currentScreenVariant = screenVariant,
                previousScreenVariant = previousScreenVariant
            )
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