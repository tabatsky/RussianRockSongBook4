package jatx.russianrocksongbook.textsearch.internal.view.textsearchsongtext

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
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
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.OpenVkMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYandexMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYoutubeMusic
import jatx.russianrocksongbook.commonviewmodel.SendWarning
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.view.dialogs.DeleteToTrashDialog
import jatx.russianrocksongbook.textsearch.internal.viewmodel.SaveSong
import jatx.russianrocksongbook.textsearch.internal.viewmodel.SelectSong
import jatx.russianrocksongbook.textsearch.internal.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.textsearch.internal.viewmodel.SetEditorMode
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel
import jatx.russianrocksongbook.textsearch.internal.viewmodel.UploadCurrentToCloud
import kotlinx.coroutines.launch

@Composable
internal fun TextSearchSongTextScreenImpl(position: Int) {
    val textSearchViewModel = TextSearchViewModel.getInstance()
    val theme = LocalAppTheme.current

    val textSearchState by textSearchViewModel.textSearchStateFlow.collectAsState()

    val song = textSearchState.currentSong
    Log.e("outOfTheBox", song?.outOfTheBox.toString())

    LaunchedEffect(position) {
        textSearchViewModel.submitAction(SelectSong(position))
    }

    val skipBody = position != textSearchState.currentSongPosition

    val key = song?.artist to song?.title
    var lastKey by rememberSaveable { mutableStateOf(key) }
    val keyChanged = key != lastKey

    if (keyChanged) {
        lastKey = key
    }

    var text by textSearchViewModel.editorText

    val onTextChange: (String) -> Unit = { text = it }

    val isAutoPlayMode = textSearchState.isAutoPlayMode
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val dY = (10 * textSearchViewModel.settings.scrollSpeed).toInt()

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

    val isUploadButtonEnabled = textSearchState.isUploadButtonEnabled
    val onUploadClick = {
        if (isUploadButtonEnabled) {
            if (song!!.outOfTheBox) {
                textSearchViewModel.submitEffect(
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
        textSearchViewModel.submitAction(SetAutoPlayMode(false))
        textSearchViewModel.submitAction(SetEditorMode(true))
    }

    val onSaveClick = {
        song?.copy(text = text)?.let {
            textSearchViewModel.submitAction(SaveSong(it))
        }
        textSearchViewModel.submitAction(SetEditorMode(false))
    }

    val isEditorMode = textSearchState.isEditorMode

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
                    TextSearchSongTextBody(
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
                TextSearchSongTextPanel(
                    W = W,
                    H = H,
                    theme = theme,
                    isEditorMode = isEditorMode,
                    listenToMusicVariant =
                    textSearchViewModel
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
                            TextSearchSongTextActions(
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
                            TextSearchSongTextActions(
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
                if (textSearchViewModel.settings.yandexMusicDontAsk) {
                    showYandexDialog = false
                    textSearchViewModel.submitAction(OpenYandexMusic(true))
                } else {
                    YandexMusicDialog(
                        commonViewModel = textSearchViewModel,
                    ) {
                        showYandexDialog = false
                    }
                }
            }
            if (showVkDialog) {
                if (textSearchViewModel.settings.vkMusicDontAsk) {
                    showVkDialog = false
                    textSearchViewModel.submitAction(OpenVkMusic(true))
                } else {
                    VkMusicDialog(
                        commonViewModel = textSearchViewModel,
                    ) {
                        showVkDialog = false
                    }
                }
            }
            if (showYoutubeMusicDialog) {
                if (textSearchViewModel.settings.youtubeMusicDontAsk) {
                    showYoutubeMusicDialog = false
                    textSearchViewModel.submitAction(OpenYoutubeMusic(true))
                } else {
                    YoutubeMusicDialog(
                        commonViewModel = textSearchViewModel,
                    ) {
                        showYoutubeMusicDialog = false
                    }
                }
            }
            if (showUploadDialog) {
                UploadDialog(
                    onConfirm = {
                        textSearchViewModel.submitAction(UploadCurrentToCloud)
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
                        textSearchViewModel.submitAction(SendWarning(comment))
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