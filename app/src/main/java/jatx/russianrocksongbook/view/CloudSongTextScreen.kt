package jatx.russianrocksongbook.view

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
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
fun CloudSongTextScreen(mvvmViewModel: MvvmViewModel) {
    val cloudSong by mvvmViewModel.cloudSong.collectAsState()


    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showVkDialog by remember { mutableStateOf(false) }
    var showYoutubeMusicDialog by remember { mutableStateOf(false) }

    var showWarningDialog by remember { mutableStateOf(false) }

    var showChordDialog by remember { mutableStateOf(false) }
    var selectedChord by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val onVkMusicClick = {
        showVkDialog = true
    }

    val onYoutubeMusicClick = {
        showYoutubeMusicDialog = true
    }

    val onWarningClick = {
        showWarningDialog = true
    }

    val onDownloadClick = {
        mvvmViewModel.downloadCurrent()
    }

    val onLikeClick = {
        mvvmViewModel.voteForCurrent(1)
    }

    val onDislikeClick = {
        mvvmViewModel.voteForCurrent(-1)
    }

    val onDislikeLongClick = {
        Log.e("dislike", "long click")
        showDeleteDialog = true
    }

    val theme = mvvmViewModel.settings.theme

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTitleDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeTitleSp = with(LocalDensity.current) {
        fontSizeTitleDp.toSp()
    }
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    cloudSong?.apply {
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
                        CloudActions(
                            mvvmViewModel = mvvmViewModel,
                            onSongChanged = {
                                coroutineScope.launch {
                                    listState.scrollToItem(
                                        index = 0,
                                        scrollOffset = 0
                                    )
                                }
                            }
                        )
                    }
                )
                Text(
                    text = "${cloudSong!!.visibleTitle} (${cloudSong!!.artist})",
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
                    item {
                        AndroidView(
                            factory = { context ->
                                ClickableWordsTextView(context)
                            },
                            update = { view ->
                                view.text = cloudSong!!.text
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
                                scrollOffset = 0
                            )
                        }
                    }
                }

                if (mvvmViewModel.settings.footerRows == 2 && W < H) {
                    CloudFooter2Row(
                        W = W,
                        H = H,
                        theme = theme,
                        onVkMusicClick = onVkMusicClick,
                        onYoutubeMusicClick = onYoutubeMusicClick,
                        onDownloadClick = onDownloadClick,
                        onWarningClick = onWarningClick,
                        onLikeClick = onLikeClick,
                        onDislikeClick = onDislikeClick,
                        onDislikeLongClick = onDislikeLongClick
                    )
                } else if (mvvmViewModel.settings.footerRows > 0) {
                    CloudFooter1Row(
                        W = W,
                        H = H,
                        theme = theme,
                        onVkMusicClick = onVkMusicClick,
                        onYoutubeMusicClick = onYoutubeMusicClick,
                        onDownloadClick = onDownloadClick,
                        onWarningClick = onWarningClick,
                        onLikeClick = onLikeClick,
                        onDislikeClick = onDislikeClick,
                        onDislikeLongClick = onDislikeLongClick
                    )
                }
            }
            if (showVkDialog) {
                if (mvvmViewModel.settings.vkMusicDontAsk) {
                    showVkDialog = false
                    mvvmViewModel.openVkMusicCloud(true)
                } else {
                    VkMusicDialog(
                        mvvmViewModel = mvvmViewModel,
                        isCloudScreen = true
                    ) {
                        showVkDialog = false
                    }
                }
            }
            if (showYoutubeMusicDialog) {
                if (mvvmViewModel.settings.youtubeMusicDontAsk) {
                    showYoutubeMusicDialog = false
                    mvvmViewModel.openYoutubeMusicCloud(true)
                } else {
                    YoutubeMusicDialog(
                        mvvmViewModel = mvvmViewModel,
                        isCloudScreen = true
                    ) {
                        showYoutubeMusicDialog = false
                    }
                }
            }
            if (showWarningDialog) {
                WarningDialog(
                    mvvmViewModel = mvvmViewModel,
                    onConfirm = { comment ->
                        mvvmViewModel.sendWarningCloud(comment)
                    },
                    onDismiss = {
                        showWarningDialog = false
                    }
                )
            }
            if (showDeleteDialog) {
                DeleteCloudSongDialog(
                    mvvmViewModel = mvvmViewModel,
                    onConfirm = { secret1, secret2 ->
                        mvvmViewModel.deleteCurrentFromCloud(secret1, secret2)
                    },
                    onDismiss = {
                        showDeleteDialog = false
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
fun CloudActions(
    mvvmViewModel: MvvmViewModel,
    onSongChanged: () -> Unit
) {
    val position by mvvmViewModel.cloudSongPosition.collectAsState()
    val count by mvvmViewModel.cloudSongCount.collectAsState()

    IconButton(onClick = {
        mvvmViewModel.prevCloudSong()
        onSongChanged()
    }) {
        Icon(painterResource(id = R.drawable.ic_left), "")
    }
    Text(
        text = "${position + 1} / $count",
        color = Color.Black,
        fontSize = 20.sp
    )
    IconButton(onClick = {
        mvvmViewModel.nextCloudSong()
        onSongChanged()
    }) {
        Icon(painterResource(id = R.drawable.ic_right), "")
    }
}

@Composable
fun CloudFooter1Row(
    W: Dp,
    H: Dp,
    theme: Theme,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onDislikeLongClick: () -> Unit
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
        DownloadButton(
            size = A,
            theme = theme,
            onClick = onDownloadClick
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
        LikeButton(
            size = A,
            theme = theme,
            onClick = onLikeClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
        )
        DislikeButton(
            size = A,
            theme = theme,
            onClick = onDislikeClick,
            onLongClick = onDislikeLongClick
        )
    }
}

@Composable
fun CloudFooter2Row(
    W: Dp,
    H: Dp,
    theme: Theme,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onDislikeLongClick: () -> Unit
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
        DownloadButton(
            size = A,
            theme = theme,
            onClick = onDownloadClick
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
        LikeButton(
            size = A,
            theme = theme,
            onClick = onLikeClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
        DislikeButton(
            size = A,
            theme = theme,
            onClick = onDislikeClick,
            onLongClick = onDislikeLongClick
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(B)
        )
    }
}
