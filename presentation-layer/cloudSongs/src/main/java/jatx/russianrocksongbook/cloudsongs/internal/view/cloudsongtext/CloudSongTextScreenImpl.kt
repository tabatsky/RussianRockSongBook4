package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.ItemsAdapter
import jatx.russianrocksongbook.cloudsongs.internal.view.dialogs.DeleteCloudSongDialog
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.chord.ChordDialog
import jatx.russianrocksongbook.commonview.dialogs.music.VkMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YandexMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.music.YoutubeMusicDialog
import jatx.russianrocksongbook.commonview.dialogs.warning.WarningDialog
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import kotlinx.coroutines.launch

private val CLOUD_SONG_TEXT_APP_BAR_WIDTH = 96.dp

@Composable
internal fun CloudSongTextScreenImpl() {
    val cloudViewModel: CloudViewModel = viewModel()

    val position by cloudViewModel.cloudSongPosition.collectAsState()

    val cloudSongsFlow by cloudViewModel
        .cloudSongsFlow.collectAsState()

    val cloudSongItems = cloudSongsFlow?.collectAsLazyPagingItems()
    val itemsAdapter = ItemsAdapter(cloudSongItems)

    val cloudSong = itemsAdapter.getItem(position)
    val invalidateCounter by cloudViewModel.invalidateCounter.collectAsState()

    cloudViewModel.updateCloudSong(cloudSong)

    val count = itemsAdapter.size
    cloudViewModel.updateCloudSongCount(count)

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val onCloudSongChanged: () -> Unit = {
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

    val onYandexMusicClick = { showYandexDialog = true }
    val onVkMusicClick = { showVkDialog = true }
    val onYoutubeMusicClick = { showYoutubeMusicDialog = true }
    val onWarningClick = { showWarningDialog = true }
    val onDownloadClick = { cloudViewModel.downloadCurrent() }
    val onLikeClick = { cloudViewModel.voteForCurrent(1) }
    val onDislikeClick = { cloudViewModel.voteForCurrent(-1) }

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

    if (cloudSong == null) {
        CloudSongTextProgress(theme)
    }

    cloudSong?.let { _cloudSong ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val W = this.maxWidth
            val H = this.minHeight

            @Composable
            fun TheBody(modifier: Modifier) {
                CloudSongTextBody(
                    W = W,
                    H = H,
                    cloudSong = _cloudSong,
                    invalidateCounter = invalidateCounter,
                    listState = listState,
                    fontSizeTextSp = fontSizeTextSp,
                    fontSizeTitleSp = fontSizeTitleSp,
                    theme = theme,
                    modifier = modifier,
                    onWordClick = onWordClick
                )
            }


            @Composable
            fun ThePanel() {
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

            if (W < H) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = theme.colorBg)
                        .padding(bottom = 4.dp)
                ) {
                    CommonTopAppBar(
                        actions = {
                            CloudSongTextActions(
                                position = position,
                                count = count,
                                onCloudSongChanged = onCloudSongChanged
                            )
                        }
                    )

                    TheBody(Modifier.weight(1.0f))
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
                            CloudSongTextActions(
                                position = position,
                                count = count,
                                onCloudSongChanged = onCloudSongChanged
                            )
                        },
                        appBarWidth = CLOUD_SONG_TEXT_APP_BAR_WIDTH
                    )

                    TheBody(Modifier.weight(1.0f))
                    ThePanel()
                }
            }
            if (showYandexDialog) {
                if (cloudViewModel.settings.yandexMusicDontAsk) {
                    showYandexDialog = false
                    cloudViewModel.openYandexMusic(true)
                } else {
                    YandexMusicDialog(
                        commonViewModel = cloudViewModel
                    ) {
                        showYandexDialog = false
                    }
                }
            }
            if (showVkDialog) {
                if (cloudViewModel.settings.vkMusicDontAsk) {
                    showVkDialog = false
                    cloudViewModel.openVkMusic(true)
                } else {
                    VkMusicDialog(
                        commonViewModel = cloudViewModel
                    ) {
                        showVkDialog = false
                    }
                }
            }
            if (showYoutubeMusicDialog) {
                if (cloudViewModel.settings.youtubeMusicDontAsk) {
                    showYoutubeMusicDialog = false
                    cloudViewModel.openYoutubeMusic(true)
                } else {
                    YoutubeMusicDialog(
                        commonViewModel = cloudViewModel
                    ) {
                        showYoutubeMusicDialog = false
                    }
                }
            }
            if (showWarningDialog) {
                WarningDialog(
                    onConfirm = { comment ->
                        cloudViewModel.sendWarning(comment)
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
