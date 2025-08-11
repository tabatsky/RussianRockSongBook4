package jatx.russianrocksongbook.localsongs.internal.view.songlist

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.ext.crop
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.localsongs.internal.view.dialogs.VoiceHelpDialog
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SpeechRecognize
import jatx.russianrocksongbook.testing.APP_BAR_TITLE
import jatx.russianrocksongbook.whatsnewdialog.api.view.WhatsNewDialog

private const val MAX_ARTIST_LENGTH_LANDSCAPE = 12
private const val MAX_ARTIST_LENGTH_PORTRAIT = 15

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
internal fun SongListContent(
    openDrawer: () -> Unit,
    currentArtist: String,
    songList: List<Song>,
    scrollPosition: Int,
    needScroll: Boolean,
    voiceHelpDontAsk: Boolean,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val W = configuration.screenWidthDp.dp
        val H = configuration.screenHeightDp.dp

        val isPortrait = W < H
        var isLastOrientationPortrait by rememberSaveable { mutableStateOf(isPortrait) }

        val wasOrientationChanged = isPortrait != isLastOrientationPortrait

        if (wasOrientationChanged) {
            LaunchedEffect(Unit) {
                isLastOrientationPortrait = isPortrait
                submitAction(UpdateSongListNeedScroll(true))
            }
        }

        if (wasOrientationChanged) return@Box

        val navigationFocusRequester = remember { FocusRequester() }

        @Composable
        fun TheBody() {
            SongListBody(
                navigationFocusRequester = navigationFocusRequester,
                songList = songList,
                scrollPosition = scrollPosition,
                needScroll = needScroll,
                submitAction = submitAction
            )
        }

        if (W < H) {
            val visibleArtist = currentArtist.crop(MAX_ARTIST_LENGTH_PORTRAIT)

            Column(
                modifier = Modifier
                    .background(theme.colorBg)
                    .fillMaxSize()
            ) {
                CommonTopAppBar(
                    title = visibleArtist,
                    titleTestTag = APP_BAR_TITLE,
                    navigationIcon = {
                        SongListNavigationIcon(
                            onClick = openDrawer,
                            focusRequester = navigationFocusRequester
                        )
                    },
                    actions = {
                        SongListActions(submitAction)
                    }
                )

                TheBody()

                WhatsNewDialog()
            }
        } else {
            val visibleArtist = currentArtist.crop(MAX_ARTIST_LENGTH_LANDSCAPE)

            Row(
                modifier = Modifier
                    .background(theme.colorBg)
                    .fillMaxSize()
            ) {
                CommonSideAppBar(
                    title = visibleArtist,
                    titleTestTag = APP_BAR_TITLE,
                    navigationIcon = {
                        SongListNavigationIcon(
                            onClick = openDrawer,
                            focusRequester = navigationFocusRequester
                        )
                    },
                    actions = {
                        SongListActions(submitAction)
                    }
                )

                TheBody()

                WhatsNewDialog()
            }
        }

        var showVoiceHelpDialog by rememberSaveable { mutableStateOf(false) }
        val onVoiceButtonClick = {
            showVoiceHelpDialog = true
        }

        VoiceButton(
            modifier = Modifier
                .focusProperties { canFocus = false }
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            theme = theme,
            onClick = onVoiceButtonClick
        )

        if (showVoiceHelpDialog) {
            if (voiceHelpDontAsk) {
                LaunchedEffect(Unit) {
                    showVoiceHelpDialog = false
                    submitAction(SpeechRecognize(true))
                }
            } else {
                VoiceHelpDialog(
                    onConfirm = { submitAction(SpeechRecognize(it)) },
                    onDismiss = {
                        showVoiceHelpDialog = false
                    }
                )
            }
        }
    }
}