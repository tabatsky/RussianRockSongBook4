package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import jatx.clickablewordstextcompose.api.Word
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.chord.ChordDialog
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.commonview.dialogs.music.VkMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YandexMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YoutubeMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.warning.WarningDialog
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonviewmodel.OpenVkMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYandexMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYoutubeMusic
import jatx.russianrocksongbook.commonviewmodel.SendWarning
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.view.dialogs.DeleteToTrashDialog
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SaveSong
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SelectSong
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SetEditorMode
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateCurrentSong
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UploadCurrentToCloud
import kotlinx.coroutines.launch

@Composable
internal fun SongTextScreenImpl(artist: String, position: Int) {
    val localViewModel = LocalViewModel.getInstance()

    val localState by localViewModel.localState.collectAsState()

    val song = localState.currentSong

    LaunchedEffect(artist to position) {
        localViewModel.submitAction(SelectSong(position))
    }

    val skipBody = position != localState.currentSongPosition

    val key = artist to song?.title
    var lastKey by rememberSaveable { mutableStateOf(key) }
    val keyChanged = key != lastKey

    if (keyChanged) {
        lastKey = key
    }

    var text by localViewModel.editorText

    val onTextChange: (String) -> Unit = { text = it }

    val isAutoPlayMode = localState.isAutoPlayMode
    val listState = rememberLazyListState()
    val tvListState = rememberTvLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val dY = (10 * localViewModel.settings.scrollSpeed).toInt()

    val onSongChanged: () -> Unit = {
        coroutineScope.launch {
            listState.scrollToItem(
                index = 0,
                scrollOffset = 0
            )
            tvListState.scrollToItem(
                index = 0,
                scrollOffset = 0
            )
        }
    }

    var showYandexDialog by rememberSaveable { mutableStateOf(false) }
    var showVkDialog by rememberSaveable { mutableStateOf(false) }
    var showYoutubeMusicDialog by rememberSaveable { mutableStateOf(false) }

    var showUploadDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteToTrashDialog by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by rememberSaveable { mutableStateOf(false) }

    var showChordDialog by rememberSaveable { mutableStateOf(false) }
    var selectedChord by rememberSaveable { mutableStateOf("") }
    val onWordClick: (Word) -> Unit = {
        selectedChord = it.text
        showChordDialog = true
    }

    val onYandexMusicClick = { showYandexDialog = true }
    val onVkMusicClick = { showVkDialog = true }
    val onYoutubeMusicClick = { showYoutubeMusicDialog = true }

    val isUploadButtonEnabled = localState.isUploadButtonEnabled
    val onUploadClick = {
        if (isUploadButtonEnabled) {
            if (song!!.outOfTheBox) {
                localViewModel.submitEffect(
                    ShowToastWithResource(R.string.toast_song_is_out_of_the_box)
                )
            } else {
                showUploadDialog = true
            }
        }
    }

    val onTrashClick = { showDeleteToTrashDialog = true }
    val onWarningClick = { showWarningDialog = true }

    val onEditClick =  {
        localViewModel.submitAction(SetAutoPlayMode(false))
        localViewModel.submitAction(SetEditorMode(true))
    }

    val onSaveClick = {
        song?.copy(text = text)?.let {
            localViewModel.submitAction(UpdateCurrentSong(it))
            localViewModel.submitAction(SaveSong(it))
        }
        localViewModel.submitAction(SetEditorMode(false))
    }

    val theme = localViewModel.theme.collectAsState().value
    val isEditorMode = localState.isEditorMode

    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(localViewModel.fontScaler, ScalePow.TEXT)
    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(localViewModel.fontScaler, ScalePow.TEXT)

    song?.let { _song ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val W = this.maxWidth
            val H = this.maxHeight

            @Composable
            fun TheBody(modifier: Modifier) {
                if (!keyChanged) {
                    SongTextBody(
                        W = W,
                        H = H,
                        song = _song,
                        text = text,
                        isEditorMode = isEditorMode,
                        listState = listState,
                        tvListState = tvListState,
                        fontSizeTextSp = fontSizeTextSp,
                        fontSizeTitleSp = fontSizeTitleSp,
                        theme = theme,
                        modifier = modifier,
                        isAutoPlayMode = isAutoPlayMode,
                        dY = dY,
                        onTextChange = onTextChange,
                        onWordClick = onWordClick
                    )
                }
            }

            @Composable
            fun ThePanel() {
                SongTextPanel(
                    W = W,
                    H = H,
                    theme = theme,
                    isEditorMode = isEditorMode,
                    listenToMusicVariant =
                    localViewModel
                        .settings
                        .listenToMusicVariant,
                    onYandexMusicClick = onYandexMusicClick,
                    onVkMusicClick = onVkMusicClick,
                    onYoutubeMusicClick = onYoutubeMusicClick,
                    onUploadClick = onUploadClick,
                    onWarningClick = onWarningClick,
                    onTrashClick = onTrashClick,
                    onEditClick = onEditClick,
                    onSaveClick = onSaveClick
                )
            }

            if (W < H) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = theme.colorBg)
                        .padding(bottom = 4.dp)
                ) {
                    CommonTopAppBar(
                        actions = {
                            SongTextActions(
                                isFavorite = _song.favorite,
                                onSongChanged = onSongChanged
                            )
                        }
                    )

                    if (skipBody) {
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .background(theme.colorBg)
                        )
                    } else {
                        TheBody(Modifier.weight(1.0f))
                    }

                    ThePanel()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = theme.colorBg)
                        .padding(end = 4.dp)
                ) {
                    CommonSideAppBar(
                        actions = {
                            SongTextActions(
                                isFavorite = _song.favorite,
                                onSongChanged = onSongChanged
                            )
                        }
                    )

                    if (skipBody) {
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .background(theme.colorBg)
                        )
                    } else {
                        TheBody(Modifier.weight(1.0f))
                    }
                    ThePanel()
                }
            }

            if (showYandexDialog) {
                if (localViewModel.settings.yandexMusicDontAsk) {
                    showYandexDialog = false
                    localViewModel.submitAction(OpenYandexMusic(true))
                } else {
                    YandexMusicDialog(
                        commonViewModel = localViewModel,
                    ) {
                        showYandexDialog = false
                    }
                }
            }
            if (showVkDialog) {
                if (localViewModel.settings.vkMusicDontAsk) {
                    showVkDialog = false
                    localViewModel.submitAction(OpenVkMusic(true))
                } else {
                    VkMusicDialog(
                        commonViewModel = localViewModel,
                    ) {
                        showVkDialog = false
                    }
                }
            }
            if (showYoutubeMusicDialog) {
                if (localViewModel.settings.youtubeMusicDontAsk) {
                    showYoutubeMusicDialog = false
                    localViewModel.submitAction(OpenYoutubeMusic(true))
                } else {
                    YoutubeMusicDialog(
                        commonViewModel = localViewModel,
                    ) {
                        showYoutubeMusicDialog = false
                    }
                }
            }
            if (showUploadDialog) {
                UploadDialog(
                    onConfirm = {
                        localViewModel.submitAction(UploadCurrentToCloud)
                    },
                    onDismiss = {
                        showUploadDialog = false
                    }
                )
            }
            if (showDeleteToTrashDialog) {
                DeleteToTrashDialog {
                    showDeleteToTrashDialog = false
                }
            }
            if (showWarningDialog) {
                WarningDialog(
                    onConfirm = { comment ->
                        localViewModel.submitAction(SendWarning(comment))
                    },
                    onDismiss = {
                        showWarningDialog = false
                    }
                )
            }
            if (showChordDialog) {
                ChordDialog(chord = selectedChord) {
                    showChordDialog = false
                }
            }
        }
    }
}