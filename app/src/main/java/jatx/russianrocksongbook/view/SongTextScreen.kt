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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import jatx.clickablewordstextview.ClickableWordsTextView
import jatx.clickablewordstextview.OnWordClickListener
import jatx.clickablewordstextview.Word
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.ScalePow
import jatx.russianrocksongbook.data.Theme
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
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

    var showVkDialog by remember { mutableStateOf(false) }
    var showYoutubeMusicDialog by remember { mutableStateOf(false) }

    var showUploadDialog by remember { mutableStateOf(false) }
    var showDeleteToTrashDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    var showChordDialog by remember { mutableStateOf(false) }
    var selectedChord by remember { mutableStateOf("") }

    val onVkMusicClick = {
        showVkDialog = true
    }

    val onYoutubeMusicClick = {
        showYoutubeMusicDialog = true
    }

    val isUploadButtonEnabled by mvvmViewModel.isUploadButtonEnabled.collectAsState()
    val onUploadClick = if (isUploadButtonEnabled) {
        { showUploadDialog = true }
    } else {
        {}
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
            mvvmViewModel.saveSong(this.withText(text))
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
            val H = this.minHeight
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
                                            selectedChord = word.text
                                            showChordDialog = true
                                        }
                                    }
                                }
                            )

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

                if (mvvmViewModel.settings.footerRows == 2 && W < H) {
                    Footer2Row(
                        W = W,
                        H = H,
                        theme = theme,
                        isEditorMode = isEditorMode,
                        onVkMusicClick = onVkMusicClick,
                        onYoutubeMusicClick = onYoutubeMusicClick,
                        onUploadClick = onUploadClick,
                        onWarningClick = onWarningClick,
                        onTrashClick = onTrashClick,
                        onEditClick = onEditClick,
                        onSaveClick = onSaveClick
                    )
                } else if (mvvmViewModel.settings.footerRows > 0) {
                    Footer1Row(
                        W = W,
                        H = H,
                        theme = theme,
                        isEditorMode = isEditorMode,
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
                if (song!!.outOfTheBox) {
                    mvvmViewModel.showToast(R.string.song_is_out_of_the_box)
                    showUploadDialog = false
                } else {
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
fun Footer1Row(
    W: Dp,
    H: Dp,
    theme: Theme,
    isEditorMode: Boolean,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onUploadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onTrashClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var A = W * 3.0f / 21
    if (W >= H) A *= 2.0f / 3
    val C = (W - A * 6.0f) / 5

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(A)
    ) {
        VkMusicButton(
            size = A,
            theme = theme,
            onClick = onVkMusicClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
        )
        YoutubeMusicButton(
            size = A,
            theme = theme,
            onClick = onYoutubeMusicClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
        )
        UploadButton(
            size = A,
            theme = theme,
            onClick = onUploadClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
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
fun Footer2Row(
    W: Dp,
    H: Dp,
    theme: Theme,
    isEditorMode: Boolean,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onUploadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onTrashClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var A = if (W < H) {
        W * 5.0f / 21
    } else {
        W * 3.0f / 21
    }
    if (W >= H) A *= 2.0f / 3
    val B = (W - A * 3.0f) / 4


    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(A)
    ) {
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
        VkMusicButton(
            size = A,
            theme = theme,
            onClick = onVkMusicClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
        YoutubeMusicButton(
            size = A,
            theme = theme,
            onClick = onYoutubeMusicClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
        UploadButton(
            size = A,
            theme = theme,
            onClick = onUploadClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(B)
    )
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(A)
    ) {
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
        WarningButton(
            size = A,
            theme = theme,
            onClick = onWarningClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
        TrashButton(
            size = A,
            theme = theme,
            onClick = onTrashClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
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
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
    }
}
