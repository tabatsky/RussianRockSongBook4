package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.chord.ChordDialog
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.commonview.dialogs.music.VkMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YandexMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YoutubeMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.warning.WarningDialog
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.view.dialogs.DeleteToTrashDialog
import jatx.russianrocksongbook.localsongs.internal.view.songtext.SongTextActions
import jatx.russianrocksongbook.localsongs.internal.view.songtext.SongTextBody
import jatx.russianrocksongbook.localsongs.internal.view.songtext.SongTextPanel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import kotlinx.coroutines.launch

@Composable
fun SongTextScreen() {
    val localViewModel: LocalViewModel = viewModel()

    val song by localViewModel.currentSong.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    song?.let {
        text = it.text
    }
    val onTextChange: (String) -> Unit = { text = it }

    val isAutoPlayMode = localViewModel.isAutoPlayMode
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val dY = (10 * localViewModel.settings.scrollSpeed).toInt()

    val onSongChanged: () -> Unit = {
        coroutineScope.launch {
            listState.scrollToItem(
                index = 0,
                scrollOffset = 0
            )
        }
    }

    var showYandexDialog by remember { mutableStateOf(false) }
    var showVkDialog by remember { mutableStateOf(false) }
    var showYoutubeMusicDialog by remember { mutableStateOf(false) }

    var showUploadDialog by remember { mutableStateOf(false) }
    var showDeleteToTrashDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    var showChordDialog by remember { mutableStateOf(false) }
    var selectedChord by remember { mutableStateOf("") }
    val onWordClick: (Word) -> Unit = {
        selectedChord = it.text
        showChordDialog = true
    }

    val onYandexMusicClick = {
        showYandexDialog = true
    }

    val onVkMusicClick = {
        showVkDialog = true
    }

    val onYoutubeMusicClick = {
        showYoutubeMusicDialog = true
    }

    val isUploadButtonEnabled by localViewModel.isUploadButtonEnabled.collectAsState()
    val onUploadClick = {
        if (isUploadButtonEnabled) {
            if (song!!.outOfTheBox) {
                localViewModel.showToast(R.string.song_is_out_of_the_box)
            } else {
                showUploadDialog = true
            }
        }
    }

    val onTrashClick = {
        showDeleteToTrashDialog = true
    }

    val onWarningClick = {
        showWarningDialog = true
    }

    val onEditClick =  {
        localViewModel.setAutoPlayMode(false)
        localViewModel.setEditorMode(true)
    }

    val onSaveClick = {
        song?.apply {
            this.text = text
            localViewModel.saveSong(this)
        }
        localViewModel.setEditorMode(false)
    }

    val theme = localViewModel.settings.theme
    val isEditorMode by localViewModel.isEditorMode.collectAsState()

    val fontScale = localViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTitleDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeTitleSp = with(LocalDensity.current) {
        fontSizeTitleDp.toSp()
    }
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    song?.let {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val W = this.maxWidth
            val H = this.maxHeight

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
                                isFavorite = it.favorite,
                                onSongChanged = onSongChanged
                            )
                        }
                    )

                    SongTextBody(
                        W = W,
                        H = H,
                        song = it,
                        text = text,
                        isEditorMode = isEditorMode,
                        listState = listState,
                        fontSizeTextSp = fontSizeTextSp,
                        fontSizeTitleSp = fontSizeTitleSp,
                        theme = theme,
                        modifier = Modifier
                            .weight(1.0f),
                        isAutoPlayMode = isAutoPlayMode,
                        dY = dY,
                        onTextChange = onTextChange,
                        onWordClick = onWordClick
                    )

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
                                isFavorite = it.favorite,
                                onSongChanged = onSongChanged
                            )
                        }
                    )

                    SongTextBody(
                        W = W,
                        H = H,
                        song = it,
                        text = text,
                        isEditorMode = isEditorMode,
                        listState = listState,
                        fontSizeTextSp = fontSizeTextSp,
                        fontSizeTitleSp = fontSizeTitleSp,
                        theme = theme,
                        modifier = Modifier
                            .weight(1.0f),
                        isAutoPlayMode = isAutoPlayMode,
                        dY = dY,
                        onTextChange = onTextChange,
                        onWordClick = onWordClick
                    )

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
            }

            if (showYandexDialog) {
                if (localViewModel.settings.yandexMusicDontAsk) {
                    showYandexDialog = false
                    localViewModel.openYandexMusic(true)
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
                    localViewModel.openVkMusic(true)
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
                    localViewModel.openYoutubeMusic(true)
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
                        localViewModel.uploadCurrentToCloud()
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
                        localViewModel.sendWarning(comment)
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













