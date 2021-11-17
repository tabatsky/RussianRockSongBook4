package jatx.russianrocksongbook.view

import android.graphics.Typeface
import android.util.Log
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import jatx.clickablewordstextview.ClickableWordsTextView
import jatx.clickablewordstextview.OnWordClickListener
import jatx.clickablewordstextview.Word
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.preferences.ScalePow
import jatx.russianrocksongbook.preferences.Theme
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.sideappbar.LeftAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun SongTextScreen(mvvmViewModel: MvvmViewModel) {
    val song by mvvmViewModel.currentSong.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    song?.apply {
        text = this.text
    }
    val onTextChange: (String) -> Unit = { text = it }

    val isAutoPlayMode = mvvmViewModel.isAutoPlayMode
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val interval = 250L
    val dY = (10 * mvvmViewModel.settings.scrollSpeed).toInt()

    val onSongChanged: () -> Unit = {
        Log.e("event", "song changed")

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

    val isUploadButtonEnabled by mvvmViewModel.isUploadButtonEnabled.collectAsState()
    val onUploadClick = {
        if (isUploadButtonEnabled) {
            if (song!!.outOfTheBox) {
                mvvmViewModel.showToast(R.string.song_is_out_of_the_box)
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
        mvvmViewModel.setAutoPlayMode(false)
        mvvmViewModel.setEditorMode(true)
    }

    val onSaveClick = {
        song?.apply {
            this.text = text
            mvvmViewModel.saveSong(this)
        }
        mvvmViewModel.setEditorMode(false)
    }

    val theme = mvvmViewModel.settings.theme
    val isEditorMode by mvvmViewModel.isEditorMode.collectAsState()

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
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
                ) {
                    TopAppBar(
                        title = {},
                        backgroundColor = theme.colorCommon,
                        navigationIcon = {
                            NavigationIcon(mvvmViewModel)
                        },
                        actions = {
                            Actions(
                                mvvmViewModel = mvvmViewModel,
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
                        interval = interval,
                        dY = dY,
                        onTextChange = onTextChange,
                        onWordClick = onWordClick
                    )

                    Panel(
                        W = W,
                        H = H,
                        theme = theme,
                        isEditorMode = isEditorMode,
                        listenToMusicVariant =
                        mvvmViewModel
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
                ) {
                    LeftAppBar(
                        title = {},
                        backgroundColor = theme.colorCommon,
                        navigationIcon = {
                            NavigationIcon(mvvmViewModel)
                        },
                        actions = {
                            Actions(
                                mvvmViewModel = mvvmViewModel,
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
                        interval = interval,
                        dY = dY,
                        onTextChange = onTextChange,
                        onWordClick = onWordClick
                    )

                    Panel(
                        W = W,
                        H = H,
                        theme = theme,
                        isEditorMode = isEditorMode,
                        listenToMusicVariant =
                        mvvmViewModel
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
                if (mvvmViewModel.settings.yandexMusicDontAsk) {
                    showYandexDialog = false
                    mvvmViewModel.openYandexMusic(true)
                } else {
                    YandexMusicDialog(mvvmViewModel = mvvmViewModel) {
                        showYandexDialog = false
                    }
                }
            }
            if (showVkDialog) {
                if (mvvmViewModel.settings.vkMusicDontAsk) {
                    showVkDialog = false
                    mvvmViewModel.openVkMusic(true)
                } else {
                    VkMusicDialog(mvvmViewModel = mvvmViewModel) {
                        showVkDialog = false
                    }
                }
            }
            if (showYoutubeMusicDialog) {
                if (mvvmViewModel.settings.youtubeMusicDontAsk) {
                    showYoutubeMusicDialog = false
                    mvvmViewModel.openYoutubeMusic(true)
                } else {
                    YoutubeMusicDialog(mvvmViewModel = mvvmViewModel) {
                        showYoutubeMusicDialog = false
                    }
                }
            }
            if (showUploadDialog) {
                UploadDialog(
                    mvvmViewModel = mvvmViewModel,
                    onConfirm = {
                        mvvmViewModel.uploadCurrentToCloud()
                    },
                    onDismiss = {
                        showUploadDialog = false
                    }
                )
            }
            if (showDeleteToTrashDialog) {
                DeleteToTrashDialog(mvvmViewModel = mvvmViewModel) {
                    showDeleteToTrashDialog = false
                }
            }
            if (showWarningDialog) {
                WarningDialog(
                    mvvmViewModel = mvvmViewModel,
                    onConfirm = { comment ->
                        mvvmViewModel.sendWarning(comment)
                    },
                    onDismiss = {
                        showWarningDialog = false
                    }
                )
            }
            if (showChordDialog) {
                ChordDialog(
                    mvvmViewModel = mvvmViewModel,
                    chord = selectedChord
                ) {
                    showChordDialog = false
                }
            }
        }
    }
}

@Composable
fun Actions(
    mvvmViewModel: MvvmViewModel,
    isFavorite: Boolean,
    onSongChanged: () -> Unit
) {
    if (mvvmViewModel.isAutoPlayMode.collectAsState().value) {
        IconButton(onClick = {
            mvvmViewModel.setAutoPlayMode(false)
        }) {
            Icon(painterResource(id = R.drawable.ic_pause), "")
        }
    } else {
        val isEditorMode = mvvmViewModel.isEditorMode.collectAsState().value
        IconButton(onClick = {
            if (!isEditorMode) {
                mvvmViewModel.setAutoPlayMode(true)
            }
        }) {
            Icon(painterResource(id = R.drawable.ic_play), "")
        }
    }
    IconButton(onClick = {
        mvvmViewModel.prevSong()
        onSongChanged()
    }) {
        Icon(painterResource(id = R.drawable.ic_left), "")
    }
    if (isFavorite) {
        IconButton(onClick = {
            mvvmViewModel.setFavorite(false)
        }) {
            Icon(painterResource(id = R.drawable.ic_delete), "")
        }
    } else {
        IconButton(onClick = {
            mvvmViewModel.setFavorite(true)
        }) {
            Icon(painterResource(id = R.drawable.ic_star), "")
        }
    }
    IconButton(onClick = {
        mvvmViewModel.nextSong()
        onSongChanged()
    }) {
        Icon(painterResource(id = R.drawable.ic_right), "")
    }
}

@Composable
fun Editor(
    text: String,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        modifier = Modifier.fillMaxWidth(),
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
fun Viewer(
    song: Song,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (Word) -> Unit
) {
    AndroidView(
        factory = { context ->
            ClickableWordsTextView(context)
        },
        update = { view ->
            view.text = song.text
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
fun SongTextBody(
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
    interval: Long,
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
            interval = interval,
            dY = dY,
            onTextChange = onTextChange,
            onWordClick = onWordClick
        )
    }
}

@Composable
fun SongTextLazyColumn(
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: StateFlow<Boolean>,
    interval: Long,
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
                Editor(
                    text = text,
                    fontSizeTextSp = fontSizeTextSp,
                    theme = theme,
                    onTextChange = onTextChange
                )
            }
        } else {
            item {
                Viewer(
                    song = song,
                    theme = theme,
                    fontSizeTextSp = fontSizeTextSp,
                    onWordClick = onWordClick
                )

                val needToScroll by isAutoPlayMode.collectAsState()

                tailrec suspend fun autoScroll(listState: LazyListState) {
                    Log.e("launched effect", "autoScroll")

                    listState.scroll(MutatePriority.PreventUserInput) {
                        scrollBy(dY.toFloat())
                    }

                    delay(interval)
                    autoScroll(listState)
                }

                if (needToScroll) {
                    LaunchedEffect(Unit) {
                        Log.e("launched effect", "start")
                        autoScroll(listState)
                    }
                }
            }
        }
    }
}


@Composable
fun Panel(
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
            PanelContent(
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
            PanelContent(
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
fun PanelContent(
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
        Divider(W = W, H = H, theme = theme)
    }
    if (listenToMusicVariant.isVk) {
        VkMusicButton(
            size = A,
            theme = theme,
            onClick = onVkMusicClick
        )
        Divider(W = W, H = H, theme = theme)
    }
    if (listenToMusicVariant.isYoutube) {
        YoutubeMusicButton(
            size = A,
            theme = theme,
            onClick = onYoutubeMusicClick
        )
        Divider(W = W, H = H, theme = theme)
    }
    UploadButton(
        size = A,
        theme = theme,
        onClick = onUploadClick
    )
    Divider(W = W, H = H, theme = theme)
    WarningButton(
        size = A,
        theme = theme,
        onClick = onWarningClick
    )
    Divider(W = W, H = H, theme = theme)
    TrashButton(
        size = A,
        theme = theme,
        onClick = onTrashClick
    )
    Divider(W = W, H = H, theme = theme)
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

@Composable
fun Divider(W: Dp, H: Dp, theme: Theme) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21
    val C = if (W < H) (W - A * 6.0f) / 5 else (H - A * 6.0f) / 5

    if (W < H) {
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
                .background(theme.colorBg)
        )
    } else {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(C)
                .background(theme.colorBg)
        )
    }
}

@Composable
fun NavigationIcon(mvvmViewModel: MvvmViewModel) {
    IconButton(onClick = {
        mvvmViewModel.back { }
    }) {
        Icon(painterResource(id = R.drawable.ic_back), "")
    }
}