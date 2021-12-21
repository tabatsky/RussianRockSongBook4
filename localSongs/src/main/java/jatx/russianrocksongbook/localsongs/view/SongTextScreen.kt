package jatx.russianrocksongbook.localsongs.view

import android.graphics.Typeface
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dqt.libs.chorddroid.classes.ChordLibrary
import jatx.clickablewordstextview.ClickableWordsTextView
import jatx.clickablewordstextview.OnWordClickListener
import jatx.clickablewordstextview.Word
import jatx.russianrocksongbook.commonview.*
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.viewmodel.LocalViewModel
import jatx.russianrocksongbook.model.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.model.preferences.ScalePow
import jatx.russianrocksongbook.model.preferences.Theme
import jatx.russianrocksongbook.testing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val interval = 250L

@Composable
fun SongTextScreen(localViewModel: LocalViewModel = viewModel()) {
    val song by localViewModel.currentSong.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    song?.apply {
        text = this.text
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

    song?.apply {
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
                                isFavorite = this@apply.favorite,
                                onSongChanged = onSongChanged
                            )
                        }
                    )

                    SongTextBody(
                        W = W,
                        H = H,
                        song = this@apply,
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
                                isFavorite = this@apply.favorite,
                                onSongChanged = onSongChanged
                            )
                        }
                    )

                    SongTextBody(
                        W = W,
                        H = H,
                        song = this@apply,
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
                        mvvmViewModel = localViewModel,
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
                        mvvmViewModel = localViewModel,
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
                        mvvmViewModel = localViewModel,
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

@Composable
private fun SongTextActions(
    localViewModel: LocalViewModel = viewModel(),
    isFavorite: Boolean,
    onSongChanged: () -> Unit
) {
    if (localViewModel.isAutoPlayMode.collectAsState().value) {
        CommonIconButton(
            resId = R.drawable.ic_pause,
        ) {
            localViewModel.setAutoPlayMode(false)
        }
    } else {
        val isEditorMode = localViewModel.isEditorMode.collectAsState().value
        CommonIconButton(
            resId = R.drawable.ic_play,
        ) {
            if (!isEditorMode) {
                localViewModel.setAutoPlayMode(true)
            }
        }
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON
    ) {
        localViewModel.prevSong()
        onSongChanged()
    }
    if (isFavorite) {
        CommonIconButton(
            resId = R.drawable.ic_delete,
            testTag = DELETE_FROM_FAVORITE_BUTTON
        ) {
            localViewModel.setFavorite(false)
        }
    } else {
        CommonIconButton(
            resId = R.drawable.ic_star,
            testTag = ADD_TO_FAVORITE_BUTTON
        ) {
            localViewModel.setFavorite(true)
        }
    }
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON
    ) {
        localViewModel.nextSong()
        onSongChanged()
    }
}

@Composable
private fun SongTextEditor(
    text: String,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        modifier = Modifier
            .testTag(SONG_TEXT_EDITOR)
            .fillMaxWidth(),
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = fontSizeTextSp,
            color = theme.colorMain
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(theme.colorBg)
            ) {
                innerTextField()  //<-- Add this
            }
        },
        cursorBrush = SolidColor(theme.colorCommon)
    )
}

@Composable
private fun SongTextViewer(
    song: Song,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (Word) -> Unit
) {
    AndroidView(
        modifier = Modifier.testTag(SONG_TEXT_VIEWER),
        factory = { context ->
            ClickableWordsTextView(context)
        },
        update = { view ->
            view.text = song.text
            view.actualWordMappings = ChordLibrary.chordMappings
            view.actualWordSet = ChordLibrary.baseChords.keys
            view.setTextColor(theme.colorMain.toArgb())
            view.setBackgroundColor(theme.colorBg.toArgb())
            view.textSize = fontSizeTextSp.value
            view.typeface = Typeface.MONOSPACE
            view.onWordClickListener = object : OnWordClickListener {
                override fun onWordClick(word: Word) {
                    onWordClick(word)
                }
            }
        }
    )
}

@Composable
private fun SongTextBody(
    W: Dp,
    H: Dp,
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    fontSizeTitleSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: StateFlow<Boolean>,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (Word) -> Unit
) {
    val paddingStart = if (W > H) 20.dp else 0.dp

    Column(
        modifier = modifier
            .padding(start = paddingStart)
    ) {
        Text(
            text = "${song.title} (${song.artist})",
            color = theme.colorMain,
            fontWeight = FontWeight.W700,
            fontSize = fontSizeTitleSp
        )
        Divider(
            color = theme.colorBg,
            thickness = dimensionResource(id = R.dimen.song_text_empty)
        )
        SongTextLazyColumn(
            song = song,
            text = text,
            isEditorMode = isEditorMode,
            listState = listState,
            fontSizeTextSp = fontSizeTextSp,
            theme = theme,
            modifier = Modifier
                .weight(1.0f),
            isAutoPlayMode = isAutoPlayMode,
            dY = dY,
            onTextChange = onTextChange,
            onWordClick = onWordClick
        )
    }
}

@Composable
private fun SongTextLazyColumn(
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: StateFlow<Boolean>,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (Word) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        if (isEditorMode) {
            item {
                SongTextEditor(
                    text = text,
                    fontSizeTextSp = fontSizeTextSp,
                    theme = theme,
                    onTextChange = onTextChange
                )
            }
        } else {
            item {
                SongTextViewer(
                    song = song,
                    theme = theme,
                    fontSizeTextSp = fontSizeTextSp,
                    onWordClick = onWordClick
                )

                val needToScroll by isAutoPlayMode.collectAsState()

                tailrec suspend fun autoScroll(listState: LazyListState) {
                    listState.scroll(MutatePriority.PreventUserInput) {
                        scrollBy(dY.toFloat())
                    }

                    delay(interval)
                    autoScroll(listState)
                }

                if (needToScroll) {
                    LaunchedEffect(Unit) {
                        autoScroll(listState)
                    }
                }
            }
        }
    }
}


@Composable
private fun SongTextPanel(
    W: Dp,
    H: Dp,
    theme: Theme,
    isEditorMode: Boolean,
    listenToMusicVariant: ListenToMusicVariant,
    onYandexMusicClick: () -> Unit,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onUploadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onTrashClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21

    if (W < H) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(A)
                .background(Color.Transparent)
        ) {
            SongTextPanelContent(
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
    } else {
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .width(A)
                .background(Color.Transparent)
        ) {
            SongTextPanelContent(
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
    }
}

@Composable
private fun SongTextPanelContent(
    W: Dp,
    H: Dp,
    theme: Theme,
    isEditorMode: Boolean,
    listenToMusicVariant: ListenToMusicVariant,
    onYandexMusicClick: () -> Unit,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onUploadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onTrashClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21

    if (listenToMusicVariant.isYandex) {
        YandexMusicButton(
            size = A,
            theme = theme,
            onClick = onYandexMusicClick
        )
        CommonPanelDivider(W = W, H = H, theme = theme)
    }
    if (listenToMusicVariant.isVk) {
        VkMusicButton(
            size = A,
            theme = theme,
            onClick = onVkMusicClick
        )
        CommonPanelDivider(W = W, H = H, theme = theme)
    }
    if (listenToMusicVariant.isYoutube) {
        YoutubeMusicButton(
            size = A,
            theme = theme,
            onClick = onYoutubeMusicClick
        )
        CommonPanelDivider(W = W, H = H, theme = theme)
    }
    UploadButton(
        size = A,
        theme = theme,
        onClick = onUploadClick
    )
    CommonPanelDivider(W = W, H = H, theme = theme)
    WarningButton(
        size = A,
        theme = theme,
        onClick = onWarningClick
    )
    CommonPanelDivider(W = W, H = H, theme = theme)
    TrashButton(
        size = A,
        theme = theme,
        onClick = onTrashClick
    )
    CommonPanelDivider(W = W, H = H, theme = theme)
    if (isEditorMode) {
        SaveButton(
            size = A,
            theme = theme,
            onClick = onSaveClick
        )
    } else {
        EditButton(
            size = A,
            theme = theme,
            onClick = onEditClick
        )
    }
}

