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
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.launch

@Composable
fun SongTextScreen(mvvmViewModel: MvvmViewModel) {
    val song by mvvmViewModel.currentSong.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    song?.apply {
        text = this.text
    }

    var y by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showYandexDialog by remember { mutableStateOf(false) }
    var showVkDialog by remember { mutableStateOf(false) }
    var showYoutubeMusicDialog by remember { mutableStateOf(false) }

    var showUploadDialog by remember { mutableStateOf(false) }
    var showDeleteToTrashDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    var showChordDialog by remember { mutableStateOf(false) }
    var selectedChord by remember { mutableStateOf("") }

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
    val isAutoPlayMode by mvvmViewModel.isAutoPlayMode.collectAsState()

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
                            IconButton(onClick = {
                                mvvmViewModel.back { }
                            }) {
                                Icon(painterResource(id = R.drawable.ic_back), "")
                            }
                        },
                        actions = {
                            Actions(
                                mvvmViewModel = mvvmViewModel,
                                isFavorite = song!!.favorite,
                                onSongChanged = {
                                    Log.e("event", "song changed")
                                    Log.e("y", y.toString())

                                    y = 0

                                    coroutineScope.launch {
                                        listState.scrollToItem(
                                            index = 0,
                                            scrollOffset = y
                                        )
                                    }
                                }
                            )
                        }
                    )
                    Text(
                        text = "${song!!.title} (${song!!.artist})",
                        color = theme.colorMain,
                        fontWeight = FontWeight.W700,
                        fontSize = fontSizeTitleSp
                    )
                    Divider(
                        color = theme.colorBg,
                        thickness = dimensionResource(id = R.dimen.song_text_empty)
                    )
                    Column (
                        modifier = Modifier
                            .weight(1.0f),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1.0f)
                        ) {
                            if (isEditorMode) {
                                item {
                                    BasicTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = text,
                                        onValueChange = {
                                            text = it
                                        },
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
                            } else {
                                item {
                                    Viewer(song, theme, fontSizeTextSp) {
                                        selectedChord = it.text
                                        showChordDialog = true
                                    }

                                    coroutineScope.launch {
                                        listState.scrollToItem(
                                            index = 0,
                                            scrollOffset = y
                                        )
                                    }

                                    val interval = 250L
                                    val dY = (10 * mvvmViewModel.settings.scrollSpeed).toInt()

                                    tailrec suspend fun autoScroll(listState: LazyListState) {
                                        if (isAutoPlayMode) {
                                            y = listState.firstVisibleItemScrollOffset
                                            listState.scroll(MutatePriority.PreventUserInput) {
                                                scrollBy(dY.toFloat())
                                            }
                                        } else {
                                            y = 0
                                        }
                                        delay(interval)
                                        autoScroll(listState)
                                    }

                                    LaunchedEffect(Unit) {
                                        autoScroll(listState)
                                    }
                                }
                            }
                        }

                        Footer(
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
                            IconButton(onClick = {
                                mvvmViewModel.back { }
                            }) {
                                Icon(painterResource(id = R.drawable.ic_back), "")
                            }
                        },
                        actions = {
                            Actions(
                                mvvmViewModel = mvvmViewModel,
                                isFavorite = song!!.favorite,
                                onSongChanged = {
                                    Log.e("event", "song changed")
                                    Log.e("y", y.toString())

                                    y = 0

                                    coroutineScope.launch {
                                        listState.scrollToItem(
                                            index = 0,
                                            scrollOffset = y
                                        )
                                    }
                                }
                            )
                        }
                    )
                    Row (
                        modifier = Modifier
                            .weight(1.0f),
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1.0f)
                                .padding(start = 20.dp)
                        ) {
                            Text(
                                text = "${song!!.title} (${song!!.artist})",
                                color = theme.colorMain,
                                fontWeight = FontWeight.W700,
                                fontSize = fontSizeTitleSp
                            )
                            Divider(
                                color = theme.colorBg,
                                thickness = dimensionResource(id = R.dimen.song_text_empty)
                            )
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .weight(1.0f)
                            ) {
                                if (isEditorMode) {
                                    item {
                                        BasicTextField(
                                            modifier = Modifier.fillMaxWidth(),
                                            value = text,
                                            onValueChange = {
                                                text = it
                                            },
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
                                } else {
                                    item {
                                        Viewer(song, theme, fontSizeTextSp) {
                                            selectedChord = it.text
                                            showChordDialog = true
                                        }

                                        coroutineScope.launch {
                                            listState.scrollToItem(
                                                index = 0,
                                                scrollOffset = y
                                            )
                                        }

                                        val interval = 250L
                                        val dY = (10 * mvvmViewModel.settings.scrollSpeed).toInt()

                                        tailrec suspend fun autoScroll(listState: LazyListState) {
                                            if (isAutoPlayMode) {
                                                y = listState.firstVisibleItemScrollOffset
                                                listState.scroll(MutatePriority.PreventUserInput) {
                                                    scrollBy(dY.toFloat())
                                                }
                                            } else {
                                                y = 0
                                            }
                                            delay(interval)
                                            autoScroll(listState)
                                        }

                                        LaunchedEffect(Unit) {
                                            autoScroll(listState)
                                        }
                                    }
                                }
                            }
                        }

                        RightSidePanel(
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
        IconButton(onClick = {
            mvvmViewModel.setAutoPlayMode(true)
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
fun Footer(
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
    val C = if (W < H) (W - A * 6.0f) / 5 else (H - A * 6.0f) / 5

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(A)
            .background(Color.Transparent)
    ) {
        if (listenToMusicVariant.isYandex) {
            YandexMusicButton(
                size = A,
                theme = theme,
                onClick = onYandexMusicClick
            )
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(C)
                    .background(theme.colorBg)
            )
        }
        if (listenToMusicVariant.isVk) {
            VkMusicButton(
                size = A,
                theme = theme,
                onClick = onVkMusicClick
            )
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(C)
                    .background(theme.colorBg)
            )
        }
        if (listenToMusicVariant.isYoutube) {
            YoutubeMusicButton(
                size = A,
                theme = theme,
                onClick = onYoutubeMusicClick
            )
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(C)
                    .background(theme.colorBg)
            )
        }
        UploadButton(
            size = A,
            theme = theme,
            onClick = onUploadClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
                .background(theme.colorBg)
        )
        WarningButton(
            size = A,
            theme = theme,
            onClick = onWarningClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
                .background(theme.colorBg)
        )
        TrashButton(
            size = A,
            theme = theme,
            onClick = onTrashClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
                .background(theme.colorBg)
        )
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
}

@Composable
fun RightSidePanel(
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
    val C = if (W < H) (W - A * 6.0f) / 5 else (H - A * 6.0f) / 5

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .width(A)
            .background(Color.Transparent)
    ) {
        if (listenToMusicVariant.isYandex) {
            YandexMusicButton(
                size = A,
                theme = theme,
                onClick = onYandexMusicClick
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(C)
                    .background(theme.colorBg)
            )
        }
        if (listenToMusicVariant.isVk) {
            VkMusicButton(
                size = A,
                theme = theme,
                onClick = onVkMusicClick
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(C)
                    .background(theme.colorBg)
            )
        }
        if (listenToMusicVariant.isYoutube) {
            YoutubeMusicButton(
                size = A,
                theme = theme,
                onClick = onYoutubeMusicClick
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(C)
                    .background(theme.colorBg)
            )
        }
        UploadButton(
            size = A,
            theme = theme,
            onClick = onUploadClick
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(C)
                .background(theme.colorBg)
        )
        WarningButton(
            size = A,
            theme = theme,
            onClick = onWarningClick
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(C)
                .background(theme.colorBg)
        )
        TrashButton(
            size = A,
            theme = theme,
            onClick = onTrashClick
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(C)
                .background(theme.colorBg)
        )
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
}

@Composable
fun Viewer(
    song: Song?,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClickLambda: (Word) -> Unit
) {
    AndroidView(
        factory = { context ->
            ClickableWordsTextView(context)
        },
        update = { view ->
            view.text = song!!.text
            view.setTextColor(theme.colorMain.toArgb())
            view.setBackgroundColor(theme.colorBg.toArgb())
            view.textSize = fontSizeTextSp.value
            view.typeface = Typeface.MONOSPACE
            view.onWordClickListener = object : OnWordClickListener {
                override fun onWordClick(word: Word) {
                    onWordClickLambda(word)
                }
            }
        }
    )
}