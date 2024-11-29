package jatx.russianrocksongbook.commonsongtext.viewmodel

import androidx.compose.runtime.mutableStateOf
import jatx.russianrocksongbook.commonview.viewmodel.DeleteCurrentToTrash
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.navigation.ScreenVariant

abstract class CommonSongTextViewModel(
    private val commonSongTextStateHolder: CommonSongTextStateHolder,
    commonSongTextViewModelDeps: CommonSongTextViewModelDeps
): CommonViewModel(
    commonSongTextStateHolder.appStateHolder,
    commonSongTextViewModelDeps.commonViewModelDeps
) {
    protected val getCountByArtistUseCase =
        commonSongTextViewModelDeps.getCountByArtistUseCase
    protected val getSongByArtistAndPositionUseCase =
        commonSongTextViewModelDeps.getSongByArtistAndPositionUseCase
    protected val updateSongUseCase =
        commonSongTextViewModelDeps.updateSongUseCase
    protected val deleteSongToTrashUseCase =
        commonSongTextViewModelDeps.deleteSongToTrashUseCase

    val commonSongTextStateFlow by lazy {
        commonSongTextStateHolder.commonSongTextStateFlow
    }

    val editorText = mutableStateOf("")

    override val currentMusic: Music?
        get() = commonSongTextStateFlow.value.currentSong

    override val currentWarnable: Warnable?
        get() = commonSongTextStateFlow.value.currentSong

    override fun handleAction(action: UIAction) {
        when (action) {
            is SelectSong -> selectSong(action.position)
            is NextSong -> nextSong()
            is PrevSong -> prevSong()
            is SaveSong -> saveSong(action.song)
            is SetFavorite -> setFavorite(action.favorite)
            is DeleteCurrentToTrash -> deleteCurrentToTrash()
            is UploadCurrentToCloud -> uploadCurrentToCloud()
            is UpdateCurrentSong -> updateCurrentSong(action.song, commonSongTextStateFlow.value.currentSongPosition)
            is SetEditorMode -> setEditorMode(action.isEditor)
            is SetAutoPlayMode -> setAutoPlayMode(action.isAutoPlay)
            is UpdateSongListScrollPosition -> updateSongListScrollPosition(action.position)
            is UpdateSongListNeedScroll -> updateSongListNeedScroll(action.need)
            else -> super.handleAction(action)
        }
    }

    abstract fun selectSong(position: Int)

    abstract fun getSongTextScreenVariant(position: Int): ScreenVariant

    abstract fun setFavorite(favorite: Boolean)

    abstract fun deleteCurrentToTrash()

    abstract fun saveSong(song: Song)

    protected fun updateCurrentSongCount(count: Int) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(currentSongCount = count)
        changeCommonSongTextState(newState)
    }

    protected fun updateCurrentSong(song: Song?, position: Int) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(
            currentSong = song,
            currentSongPosition = position,
            songListScrollPosition = position,
            songListNeedScroll = true
        )
        changeCommonSongTextState(newState)
    }

    protected fun updateCurrentSong(song: Song?) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(
            currentSong = song
        )
        changeCommonSongTextState(newState)
    }

    protected fun updateCurrentSongPosition(position: Int) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(
            currentSongPosition = position,
            songListScrollPosition = position,
            songListNeedScroll = true
        )
        changeCommonSongTextState(newState)
    }

    protected fun setEditorMode(editorMode: Boolean) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(isEditorMode = editorMode)
        changeCommonSongTextState(newState)
    }

    protected fun setAutoPlayMode(autoPlayMode: Boolean) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(isAutoPlayMode = autoPlayMode)
        changeCommonSongTextState(newState)
    }

    protected fun updateSongListScrollPosition(position: Int) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(songListScrollPosition = position)
        changeCommonSongTextState(newState)
    }

    protected fun updateSongListNeedScroll(needScroll: Boolean) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(songListNeedScroll = needScroll)
        changeCommonSongTextState(newState)
    }

    protected fun updateEditorText(text: String) {
        editorText.value = text
    }

    private fun nextSong() {
        with (commonSongTextStateFlow.value) {
            if (currentSongCount > 0) {
                selectScreen(getSongTextScreenVariant(
                    position = (currentSongPosition + 1) % currentSongCount))
            }
        }
    }

    private fun prevSong() {
        with (commonSongTextStateFlow.value) {
            if (currentSongCount > 0) {
                if (currentSongPosition > 0) {
                    selectScreen(getSongTextScreenVariant(
                        position = (currentSongPosition - 1) % currentSongCount))
                } else {
                    selectScreen(getSongTextScreenVariant(
                        position = currentSongCount - 1)
                    )
                }
            }
        }
    }

    private fun uploadCurrentToCloud() {
        commonSongTextStateFlow.value.currentSong?.let { song ->
            uploadSongToCloud(song) {
                setUploadButtonEnabled(it)
            }
        }
    }

    private fun setUploadButtonEnabled(enabled: Boolean) {
        val localState = commonSongTextStateFlow.value
        val newState = localState.copy(isUploadButtonEnabled = enabled)
        changeCommonSongTextState(newState)
    }

    private fun changeCommonSongTextState(localState: CommonSongTextState) =
        commonSongTextStateHolder.changeCommonSongTextState(localState)
}