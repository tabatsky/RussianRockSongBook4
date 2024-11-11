package jatx.russianrocksongbook.commonview.songtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import jatx.clickablewordstextcompose.api.Word
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.chord.ChordDialog
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.commonview.dialogs.delete.DeleteToTrashDialog
import jatx.russianrocksongbook.commonview.dialogs.music.VkMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YandexMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YoutubeMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.warning.WarningDialog
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.OpenVkMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYandexMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYoutubeMusic
import jatx.russianrocksongbook.commonviewmodel.SaveSong
import jatx.russianrocksongbook.commonviewmodel.SelectSong
import jatx.russianrocksongbook.commonviewmodel.SendWarning
import jatx.russianrocksongbook.commonviewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.commonviewmodel.SetEditorMode
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.commonviewmodel.UIEffect
import jatx.russianrocksongbook.commonviewmodel.UpdateCurrentSong
import jatx.russianrocksongbook.commonviewmodel.UploadCurrentToCloud
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import kotlinx.coroutines.launch

@Composable
fun CommonSongTextScreenImplContent(
    artist: String,
    position: Int,
    song: Song?,
    currentSongPosition: Int,
    isAutoPlayMode: Boolean,
    isEditorMode: Boolean,
    isUploadButtonEnabled: Boolean,
    editorText: MutableState<String>,
    scrollSpeed: Float,
    listenToMusicVariant: ListenToMusicVariant,
    vkMusicDontAsk: Boolean,
    yandexMusicDontAsk: Boolean,
    youtubeMusicDontAsk: Boolean,
    submitAction: (UIAction) -> Unit,
    submitEffect: (UIEffect) -> Unit
) {
    val theme = LocalAppTheme.current

    LaunchedEffect(artist to position) {
        submitAction(SelectSong(position))
    }

    val skipBody = position != currentSongPosition

    val key = artist to song?.title
    var lastKey by rememberSaveable { mutableStateOf(key) }
    val keyChanged = key != lastKey

    if (keyChanged) {
        lastKey = key
    }

    var text by editorText

    val onTextChange: (String) -> Unit = { text = it }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val dY = (10 * scrollSpeed).toInt()

    val onSongChanged: () -> Unit = {
        coroutineScope.launch {
            listState.scrollToItem(
                index = 0,
                scrollOffset = 0
            )
        }
    }

    var showYandexDialog by rememberSaveable { mutableStateOf(false) }
    var showVkDialog by rememberSaveable { mutableStateOf(false) }
    var showYoutubeDialog by rememberSaveable { mutableStateOf(false) }

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
    val onYoutubeMusicClick = { showYoutubeDialog = true }

    val onUploadClick = {
        if (isUploadButtonEnabled) {
            if (song!!.outOfTheBox) {
                submitEffect(
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
        submitAction(SetAutoPlayMode(false))
        submitAction(SetEditorMode(true))
    }

    val onSaveClick = {
        song?.copy(text = text)?.let {
            submitAction(UpdateCurrentSong(it))
            submitAction(SaveSong(it))
        }
        submitAction(SetEditorMode(false))
    }

    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(ScalePow.TEXT)

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
                    CommonSongTextBody(
                        W = W,
                        H = H,
                        song = _song,
                        text = text,
                        isEditorMode = isEditorMode,
                        listState = listState,
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
                CommonSongTextPanel(
                    W = W,
                    H = H,
                    theme = theme,
                    isEditorMode = isEditorMode,
                    listenToMusicVariant = listenToMusicVariant,
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

            @Composable
            fun TheActions() {
                CommonSongTextActions(
                    isFavorite = _song.favorite,
                    onSongChanged = onSongChanged,
                    isAutoPlayMode = isAutoPlayMode,
                    isEditorMode = isEditorMode,
                    submitAction = submitAction
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
                        actions = { TheActions() }
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
                        actions = { TheActions() }
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
                if (yandexMusicDontAsk) {
                    showYandexDialog = false
                    submitAction(OpenYandexMusic(true))
                } else {
                    YandexMusicDialog(
                        submitAction = submitAction,
                        onDismiss = {
                            showYandexDialog = false
                        })
                }
            }
            if (showVkDialog) {
                if (vkMusicDontAsk) {
                    showVkDialog = false
                    submitAction(OpenVkMusic(true))
                } else {
                    VkMusicDialog(
                        submitAction = submitAction,
                        onDismiss = {
                            showVkDialog = false
                        })
                }
            }
            if (showYoutubeDialog) {
                if (youtubeMusicDontAsk) {
                    showYoutubeDialog = false
                    submitAction(OpenYoutubeMusic(true))
                } else {
                    YoutubeMusicDialog(
                        submitAction = submitAction,
                        onDismiss = {
                            showYoutubeDialog = false
                        })
                }
            }
            if (showUploadDialog) {
                UploadDialog(
                    onConfirm = {
                        submitAction(UploadCurrentToCloud)
                    },
                    onDismiss = {
                        showUploadDialog = false
                    }
                )
            }
            if (showDeleteToTrashDialog) {
                DeleteToTrashDialog(
                    onDismiss = {
                        showDeleteToTrashDialog = false
                    },
                    submitAction = submitAction
                )
            }
            if (showWarningDialog) {
                WarningDialog(
                    onConfirm = { comment ->
                        submitAction(SendWarning(comment))
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