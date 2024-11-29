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
            is UpdateShowVkDialog -> updateShowVkDialog(action.needShow)
            is UpdateShowYandexDialog -> updateShowYandexDialog(action.needShow)
            is UpdateShowYoutubeDialog -> updateShowYoutubeDialog(action.needShow)
            is UpdateShowUploadDialog -> updateShowUploadDialog(action.needShow)
            is UpdateShowDeleteToTrashDialog -> updateShowDeleteToTrashDialog(action.needShow)
            is UpdateShowWarningDialog -> updateShowWarningDialog(action.needShow)
            is UpdateShowChordDialog -> updateShowChordDialog(action.needShow, action.selectedChord)
            else -> super.handleAction(action)
        }
    }

    protected abstract fun selectSong(position: Int)

    protected abstract fun getSongTextScreenVariant(position: Int): ScreenVariant

    protected abstract fun setFavorite(favorite: Boolean)

    protected abstract fun deleteCurrentToTrash()

    protected abstract fun saveSong(song: Song)

    protected fun updateCurrentSongCount(count: Int) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(currentSongCount = count)
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
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(
            currentSong = song
        )
        changeCommonSongTextState(newState)
    }

    protected fun updateCurrentSongPosition(position: Int) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(
            currentSongPosition = position,
            songListScrollPosition = position,
            songListNeedScroll = true
        )
        changeCommonSongTextState(newState)
    }

    protected fun setEditorMode(editorMode: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(isEditorMode = editorMode)
        changeCommonSongTextState(newState)
    }

    protected fun setAutoPlayMode(autoPlayMode: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(isAutoPlayMode = autoPlayMode)
        changeCommonSongTextState(newState)
    }

    protected fun updateSongListScrollPosition(position: Int) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(songListScrollPosition = position)
        changeCommonSongTextState(newState)
    }

    protected fun updateSongListNeedScroll(needScroll: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(songListNeedScroll = needScroll)
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
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(isUploadButtonEnabled = enabled)
        changeCommonSongTextState(newState)
    }

    private fun updateShowVkDialog(needShow: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(showVkDialog = needShow)
        changeCommonSongTextState(newState)
    }

    private fun updateShowYandexDialog(needShow: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(showYandexDialog = needShow)
        changeCommonSongTextState(newState)
    }

    private fun updateShowYoutubeDialog(needShow: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(showYoutubeDialog = needShow)
        changeCommonSongTextState(newState)
    }

    private fun updateShowUploadDialog(needShow: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(showUploadDialog = needShow)
        changeCommonSongTextState(newState)
    }

    private fun updateShowDeleteToTrashDialog(needShow: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(showDeleteToTrashDialog = needShow)
        changeCommonSongTextState(newState)
    }

    private fun updateShowWarningDialog(needShow: Boolean) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(showWarningDialog = needShow)
        changeCommonSongTextState(newState)
    }

    private fun updateShowChordDialog(needShow: Boolean, chord: String) {
        val commonSongTextState = commonSongTextStateFlow.value
        val newState = commonSongTextState.copy(
            showChordDialog = needShow,
            selectedChord = chord
        )
        changeCommonSongTextState(newState)
    }

    private fun changeCommonSongTextState(commonSongTextState: CommonSongTextState) =
        commonSongTextStateHolder.changeCommonSongTextState(commonSongTextState)
}