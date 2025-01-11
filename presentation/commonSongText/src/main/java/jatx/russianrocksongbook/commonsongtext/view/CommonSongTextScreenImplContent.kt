package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import jatx.clickablewordstextcompose.api.Word
import jatx.russianrocksongbook.commonsongtext.R
import jatx.russianrocksongbook.commonsongtext.viewmodel.SaveSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.SelectSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetEditorMode
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateCurrentSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowChordDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowDeleteToTrashDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowUploadDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowVkDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowWarningDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowYandexDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateShowYoutubeDialog
import jatx.russianrocksongbook.commonsongtext.viewmodel.UploadCurrentToCloud
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
import jatx.russianrocksongbook.commonviewmodel.SendWarning
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.commonviewmodel.UIEffect
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
    isAutoPlayMode: Boolean = false,
    isEditorMode: Boolean = false,
    isUploadButtonEnabled: Boolean = true,
    scrollSpeed: Float = 1.0f,
    listenToMusicVariant: ListenToMusicVariant = ListenToMusicVariant.YANDEX_AND_YOUTUBE,
    showVkDialog: Boolean = false,
    showYandexDialog: Boolean = false,
    showYoutubeDialog: Boolean = false,
    showUploadDialog: Boolean = false,
    showDeleteToTrashDialog: Boolean = false,
    showWarningDialog: Boolean = false,
    showChordDialog: Boolean = false,
    selectedChord: String = "",
    vkMusicDontAsk: Boolean = false,
    yandexMusicDontAsk: Boolean = false,
    youtubeMusicDontAsk: Boolean = false,
    editorText: MutableState<String>,
    submitAction: (UIAction) -> Unit,
    submitEffect: (UIEffect) -> Unit
) {
    val theme = LocalAppTheme.current

    LaunchedEffect(artist to position) {
        submitAction(SelectSong(position))
    }

    val positionChanged = position != currentSongPosition

    var positionDeltaSign by rememberSaveable { mutableIntStateOf(1) }
    if (positionChanged) {
        val positionIncreased = position > currentSongPosition
        val positionWasReset =
            (position - currentSongPosition > 1) && (currentSongPosition == 0)
                    || (currentSongPosition - position > 1) && (position == 0)
        positionDeltaSign =
            (if (positionIncreased) 1 else -1) * (if (positionWasReset) -1 else 1)
    }

    val key = artist to song?.title
    var lastKey by rememberSaveable { mutableStateOf(key) }
    val keyChanged = key != lastKey

    if (keyChanged) {
        lastKey = key
    }

    val skipBody = positionChanged || keyChanged || song == null

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

    val onWordClick: (Word) -> Unit = {
        submitAction(
            UpdateShowChordDialog(
                needShow = true,
                selectedChord = it.text
            )
        )
    }

    val onYandexMusicClick = { submitAction(UpdateShowYandexDialog(true)) }
    val onVkMusicClick = { submitAction(UpdateShowVkDialog(true)) }
    val onYoutubeMusicClick = { submitAction(UpdateShowYoutubeDialog(true)) }

    val onUploadClick = {
        if (isUploadButtonEnabled) {
            if (song!!.outOfTheBox) {
                submitEffect(
                    ShowToastWithResource(R.string.toast_song_is_out_of_the_box)
                )
            } else {
                submitAction(UpdateShowUploadDialog(true))
            }
        }
    }

    val onTrashClick = { submitAction(UpdateShowDeleteToTrashDialog(true)) }
    val onWarningClick = { submitAction(UpdateShowWarningDialog(true)) }

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        @Composable
        fun TheBody(modifier: Modifier) {
            song?.let { _song ->
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
                isFavorite = song?.favorite ?: false,
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

                AnimatedContent(
                    targetState = skipBody,
                    label = "songTextBody",
                    transitionSpec = {
                        slideInHorizontally {
                            fullWidth -> fullWidth * positionDeltaSign
                        } togetherWith slideOutHorizontally {
                            fullWidth -> -fullWidth * positionDeltaSign
                        }
                    },
                    modifier = Modifier
                        .weight(1.0f)
                ) { skip ->
                    if (skip) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(theme.colorBg)
                        )
                    } else {
                        TheBody(Modifier.fillMaxSize())
                    }
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

                AnimatedContent(
                    targetState = skipBody,
                    label = "songTextBody",
                    transitionSpec = {
                        slideInHorizontally {
                                fullWidth -> fullWidth * positionDeltaSign
                        } togetherWith slideOutHorizontally {
                                fullWidth -> -fullWidth * positionDeltaSign
                        }
                    },
                    modifier = Modifier
                        .weight(1.0f)
                ) { skip ->
                    if (skip) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(theme.colorBg)
                        )
                    } else {
                        TheBody(Modifier.fillMaxSize())
                    }
                }

                ThePanel()
            }
        }

        if (showVkDialog) {
            if (vkMusicDontAsk) {
                submitAction(UpdateShowVkDialog(false))
                submitAction(OpenVkMusic(true))
            } else {
                VkMusicDialog(
                    submitAction = submitAction,
                    onDismiss = {
                        submitAction(UpdateShowVkDialog(false))
                    })
            }
        }
        if (showYandexDialog) {
            if (yandexMusicDontAsk) {
                submitAction(UpdateShowYandexDialog(false))
                submitAction(OpenYandexMusic(true))
            } else {
                YandexMusicDialog(
                    submitAction = submitAction,
                    onDismiss = {
                        submitAction(UpdateShowYandexDialog(false))
                    })
            }
        }
        if (showYoutubeDialog) {
            if (youtubeMusicDontAsk) {
                submitAction(UpdateShowYoutubeDialog(false))
                submitAction(OpenYoutubeMusic(true))
            } else {
                YoutubeMusicDialog(
                    submitAction = submitAction,
                    onDismiss = {
                        submitAction(UpdateShowYoutubeDialog(false))
                    })
            }
        }
        if (showUploadDialog) {
            UploadDialog(
                onConfirm = {
                    submitAction(UploadCurrentToCloud)
                },
                onDismiss = {
                    submitAction(UpdateShowUploadDialog(false))
                }
            )
        }
        if (showDeleteToTrashDialog) {
            DeleteToTrashDialog(
                onDismiss = {
                    submitAction(UpdateShowDeleteToTrashDialog(false))
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
                    submitAction(UpdateShowWarningDialog(false))
                }
            )
        }
        if (showChordDialog) {
            ChordDialog(chord = selectedChord) {
                submitAction(UpdateShowChordDialog(needShow = false))
            }
        }
    }
}