package jatx.russianrocksongbook.cloudsongs.view

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dqt.libs.chorddroid.classes.ChordLibrary
import jatx.clickablewordstextview.ClickableWordsTextView
import jatx.clickablewordstextview.OnWordClickListener
import jatx.clickablewordstextview.Word
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.model.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.model.preferences.ScalePow
import jatx.russianrocksongbook.model.preferences.Theme
import jatx.russianrocksongbook.cloudsongs.viewmodel.CloudViewModel
import jatx.russianrocksongbook.commonview.*
import kotlinx.coroutines.launch

private val CLOUD_SONG_TEXT_APP_BAR_WIDTH = 96.dp

@Composable
fun CloudSongTextScreen(cloudViewModel: CloudViewModel = viewModel()) {
    val cloudSong by cloudViewModel.cloudSong.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val onCloudSongChanged: () -> Unit = {
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

    val onWarningClick = {
        showWarningDialog = true
    }

    val onDownloadClick = {
        cloudViewModel.downloadCurrent()
    }

    val onLikeClick = {
        cloudViewModel.voteForCurrent(1)
    }

    val onDislikeClick = {
        cloudViewModel.voteForCurrent(-1)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val onDislikeLongClick = {
        Log.e("dislike", "long click")
        showDeleteDialog = true
    }

    val theme = cloudViewModel.settings.theme

    val fontScale = cloudViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
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

            if (W < H) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = theme.colorBg)
                ) {
                    CommonTopAppBar(
                        actions = {
                            CloudSongTextActions(
                                onCloudSongChanged = onCloudSongChanged
                            )
                        }
                    )

                    CloudSongTextBody(
                        W = W,
                        H = H,
                        cloudSong = this@apply,
                        listState = listState,
                        fontSizeTextSp = fontSizeTextSp,
                        fontSizeTitleSp = fontSizeTitleSp,
                        theme = theme,
                        modifier = Modifier
                            .weight(1.0f),
                        onWordClick = onWordClick
                    )

                    CloudSongTextPanel(
                        W = W,
                        H = H,
                        theme = theme,
                        listenToMusicVariant =
                        cloudViewModel
                            .settings
                            .listenToMusicVariant,
                        onYandexMusicClick = onYandexMusicClick,
                        onVkMusicClick = onVkMusicClick,
                        onYoutubeMusicClick = onYoutubeMusicClick,
                        onDownloadClick = onDownloadClick,
                        onWarningClick = onWarningClick,
                        onLikeClick = onLikeClick,
                        onDislikeClick = onDislikeClick,
                        onDislikeLongClick = onDislikeLongClick
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = theme.colorBg)
                ) {
                    CommonSideAppBar(
                        actions = {
                            CloudSongTextActions(
                                onCloudSongChanged = onCloudSongChanged
                            )
                        },
                        appBarWidth = CLOUD_SONG_TEXT_APP_BAR_WIDTH
                    )

                    CloudSongTextBody(
                        W = W,
                        H = H,
                        cloudSong = this@apply,
                        listState = listState,
                        fontSizeTextSp = fontSizeTextSp,
                        fontSizeTitleSp = fontSizeTitleSp,
                        theme = theme,
                        modifier = Modifier
                            .weight(1.0f),
                        onWordClick = onWordClick
                    )

                    CloudSongTextPanel(
                        W = W,
                        H = H,
                        theme = theme,
                        listenToMusicVariant =
                        cloudViewModel
                            .settings
                            .listenToMusicVariant,
                        onYandexMusicClick = onYandexMusicClick,
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
            if (showYandexDialog) {
                if (cloudViewModel.settings.yandexMusicDontAsk) {
                    showYandexDialog = false
                    cloudViewModel.openYandexMusicCloud(true)
                } else {
                    YandexMusicDialog(
                        mvvmViewModel = cloudViewModel
                    ) {
                        showYandexDialog = false
                    }
                }
            }
            if (showVkDialog) {
                if (cloudViewModel.settings.vkMusicDontAsk) {
                    showVkDialog = false
                    cloudViewModel.openVkMusicCloud(true)
                } else {
                    VkMusicDialog(
                        mvvmViewModel = cloudViewModel
                    ) {
                        showVkDialog = false
                    }
                }
            }
            if (showYoutubeMusicDialog) {
                if (cloudViewModel.settings.youtubeMusicDontAsk) {
                    showYoutubeMusicDialog = false
                    cloudViewModel.openYoutubeMusicCloud(true)
                } else {
                    YoutubeMusicDialog(
                        mvvmViewModel = cloudViewModel
                    ) {
                        showYoutubeMusicDialog = false
                    }
                }
            }
            if (showWarningDialog) {
                WarningDialog(
                    onConfirm = { comment ->
                        cloudViewModel.sendWarningCloud(comment)
                    },
                    onDismiss = {
                        showWarningDialog = false
                    }
                )
            }
            if (showDeleteDialog) {
                DeleteCloudSongDialog(
                    onConfirm = { secret1, secret2 ->
                        cloudViewModel.deleteCurrentFromCloud(secret1, secret2)
                    },
                    onDismiss = {
                        showDeleteDialog = false
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
private fun CloudSongTextActions(
    cloudViewModel: CloudViewModel = viewModel(),
    onCloudSongChanged: () -> Unit
) {
    val position by cloudViewModel.cloudSongPosition.collectAsState()
    val count by cloudViewModel.cloudSongCount.collectAsState()

    CommonIconButton(resId = R.drawable.ic_left) {
        cloudViewModel.prevCloudSong()
        onCloudSongChanged()
    }
    Text(
        text = "${position + 1} / $count",
        color = Color.Black,
        fontSize = 20.sp
    )
    CommonIconButton(resId = R.drawable.ic_right) {
        cloudViewModel.nextCloudSong()
        onCloudSongChanged()
    }
}

@Composable
private fun CloudSongTextViewer(
    cloudSong: CloudSong,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (Word) -> Unit
) {
    AndroidView(
        factory = { context ->
            ClickableWordsTextView(context)
        },
        update = { view ->
            view.text = cloudSong.text
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
private fun CloudSongTextBody(
    W: Dp,
    H: Dp,
    cloudSong: CloudSong,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    fontSizeTitleSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    onWordClick: (Word) -> Unit
) {
    val paddingStart = if (W > H) 20.dp else 0.dp

    Column(
        modifier = modifier
            .padding(start = paddingStart)
    ) {
        Text(
            text = "${cloudSong.title} (${cloudSong.artist})",
            color = theme.colorMain,
            fontWeight = FontWeight.W700,
            fontSize = fontSizeTitleSp
        )
        Divider(
            color = theme.colorBg,
            thickness = dimensionResource(id = R.dimen.song_text_empty)
        )
        CloudSongTextLazyColumn(
            cloudSong = cloudSong,
            listState = listState,
            fontSizeTextSp = fontSizeTextSp,
            theme = theme,
            modifier = Modifier
                .weight(1.0f),
            onWordClick = onWordClick
        )
    }
}

@Composable
private fun CloudSongTextLazyColumn(
    cloudSong: CloudSong,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    onWordClick: (Word) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        item {
            CloudSongTextViewer(
                cloudSong = cloudSong,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onWordClick = onWordClick
            )
        }
    }
}


@Composable
private fun CloudSongTextPanel(
    W: Dp,
    H: Dp,
    theme: Theme,
    listenToMusicVariant: ListenToMusicVariant,
    onYandexMusicClick: () -> Unit,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onDislikeLongClick: () -> Unit
) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21

    if (W < H) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(A)
                .background(Color.Transparent)
        ) {
            CloudSongTextPanelContent(
                W = W,
                H = H,
                theme = theme,
                listenToMusicVariant = listenToMusicVariant,
                onYandexMusicClick = onYandexMusicClick,
                onVkMusicClick = onVkMusicClick,
                onYoutubeMusicClick = onYoutubeMusicClick,
                onDownloadClick = onDownloadClick,
                onWarningClick = onWarningClick,
                onLikeClick = onLikeClick,
                onDislikeClick = onDislikeClick,
                onDislikeLongClick = onDislikeLongClick
            )
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .width(A)
                .background(Color.Transparent)
        ) {
            CloudSongTextPanelContent(
                W = W,
                H = H,
                theme = theme,
                listenToMusicVariant = listenToMusicVariant,
                onYandexMusicClick = onYandexMusicClick,
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
}

@Composable
private fun CloudSongTextPanelContent(
    W: Dp,
    H: Dp,
    theme: Theme,
    listenToMusicVariant: ListenToMusicVariant,
    onYandexMusicClick: () -> Unit,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onDislikeLongClick: () -> Unit
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
    DownloadButton(
        size = A,
        theme = theme,
        onClick = onDownloadClick
    )
    CommonPanelDivider(W = W, H = H, theme = theme)
    WarningButton(
        size = A,
        theme = theme,
        onClick = onWarningClick
    )
    CommonPanelDivider(W = W, H = H, theme = theme)
    LikeButton(
        size = A,
        theme = theme,
        onClick = onLikeClick
    )
    CommonPanelDivider(W = W, H = H, theme = theme)
    DislikeButton(
        size = A,
        theme = theme,
        onClick = onDislikeClick,
        onLongClick = onDislikeLongClick
    )

}