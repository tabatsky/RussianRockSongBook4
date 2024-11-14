package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.clickablewordstextcompose.api.Word
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.ItemsAdapter
import jatx.russianrocksongbook.cloudsongs.internal.view.dialogs.DeleteCloudSongDialog
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.DeleteCurrentFromCloud
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.DownloadCurrent
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SelectCloudSong
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateCurrentCloudSong
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateCurrentCloudSongCount
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.VoteForCurrent
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.chord.ChordDialog
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
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private val CLOUD_SONG_TEXT_APP_BAR_WIDTH = 96.dp

@Preview
@Composable
fun CloudSongTextScreenImplPreview() {
    val cloudSongs = (1..30)
        .map {
            CloudSong(
                artist = "Исполнитель $it",
                title = "Название $it",
                text = "Текст текст\nТекст\nAm Em\nТекст\n",
                variant = 1,
                likeCount = 2,
                dislikeCount = 1
            )
        }

    val cloudSongItems = flowOf(PagingData.from(cloudSongs)).collectAsLazyPagingItems()

    CloudSongTextScreenImplContent(
        position = 3,
        cloudSongItems = cloudSongItems,
        listenToMusicVariant = ListenToMusicVariant.YANDEX_AND_VK,
        vkMusicDontAsk = false,
        yandexMusicDontAsk = false,
        youtubeMusicDontAsk = false,
        submitAction = {}
    )
}


@Composable
internal fun CloudSongTextScreenImplContent(
    position: Int,
    cloudSongItems: LazyPagingItems<CloudSong>?,
    listenToMusicVariant: ListenToMusicVariant,
    vkMusicDontAsk: Boolean,
    yandexMusicDontAsk: Boolean,
    youtubeMusicDontAsk: Boolean,
    submitAction: (UIAction) -> Unit
) {
    val key = position
    var lastKey by rememberSaveable { mutableStateOf(key) }
    val keyChanged = key != lastKey

    if (keyChanged) {
        lastKey = key
    }

    submitAction(SelectCloudSong(position))

    val itemsAdapter = ItemsAdapter(cloudSongItems)

    val cloudSong = itemsAdapter.getItem(position)

    submitAction(UpdateCurrentCloudSong(cloudSong))

    val count = itemsAdapter.size
    submitAction(UpdateCurrentCloudSongCount(count))

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

    var showYandexDialog by rememberSaveable { mutableStateOf(false) }
    var showVkDialog by rememberSaveable { mutableStateOf(false) }
    var showYoutubeDialog by rememberSaveable { mutableStateOf(false) }

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
    val onWarningClick = { showWarningDialog = true }
    val onDownloadClick = { submitAction(DownloadCurrent) }
    val onLikeClick = { submitAction(VoteForCurrent(1)) }
    val onDislikeClick = { submitAction(VoteForCurrent(-1)) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val onDislikeLongClick = {
        Log.e("dislike", "long click")
        showDeleteDialog = true
    }

    val theme = LocalAppTheme.current

    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(ScalePow.TEXT)

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
                if (!keyChanged) {
                    CloudSongTextBody(
                        W = W,
                        H = H,
                        cloudSong = _cloudSong,
                        listState = listState,
                        fontSizeTextSp = fontSizeTextSp,
                        fontSizeTitleSp = fontSizeTitleSp,
                        theme = theme,
                        modifier = modifier,
                        onWordClick = onWordClick
                    )
                }
            }


            @Composable
            fun ThePanel() {
                CloudSongTextPanel(
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
                                onCloudSongChanged = onCloudSongChanged,
                                submitAction = submitAction
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
                                onCloudSongChanged = onCloudSongChanged,
                                submitAction = submitAction
                            )
                        },
                        appBarWidth = CLOUD_SONG_TEXT_APP_BAR_WIDTH
                    )

                    TheBody(Modifier.weight(1.0f))
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
            if (showDeleteDialog) {
                DeleteCloudSongDialog(
                    onConfirm = { secret1, secret2 ->
                        submitAction(
                            DeleteCurrentFromCloud(secret1, secret2)
                        )
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
