package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.ext.crop
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.localsongs.internal.view.dialogs.VoiceHelpDialog
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SpeechRecognize
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.testing.APP_BAR_TITLE
import jatx.russianrocksongbook.whatsnewdialog.api.view.WhatsNewDialog

private const val MAX_ARTIST_LENGTH_LANDSCAPE = 12
private const val MAX_ARTIST_LENGTH_PORTRAIT = 15

@Composable
internal fun SongListContent(
    openDrawer: () -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()

    val theme = LocalAppTheme.current

    val localState by localViewModel.localState.collectAsState()

    val artist = localState.currentArtist

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        val isPortrait = W < H
        var isLastOrientationPortrait by rememberSaveable { mutableStateOf(isPortrait) }

        val wasOrientationChanged = isPortrait != isLastOrientationPortrait

        LaunchedEffect(isPortrait) {
            if (wasOrientationChanged) {
                isLastOrientationPortrait = isPortrait
                localViewModel.submitAction(UpdateSongListNeedScroll(true))
            }
        }

        if (wasOrientationChanged) return@BoxWithConstraints

        if (W < H) {
            val visibleArtist = artist.crop(MAX_ARTIST_LENGTH_PORTRAIT)

            Column(
                modifier = Modifier
                    .background(theme.colorBg)
                    .fillMaxSize()
            ) {
                val navigationFocusRequester = remember { FocusRequester() }
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
                        SongListActions()
                    }
                )

                SongListBody(navigationFocusRequester)

                WhatsNewDialog()
            }
        } else {
            val visibleArtist = artist.crop(MAX_ARTIST_LENGTH_LANDSCAPE)

            Row(
                modifier = Modifier
                    .background(theme.colorBg)
                    .fillMaxSize()
            ) {
                val navigationFocusRequester = remember { FocusRequester() }
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
                        SongListActions()
                    }
                )

                SongListBody(navigationFocusRequester)

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
            if (localViewModel.settings.voiceHelpDontAsk) {
                showVoiceHelpDialog = false
                localViewModel.submitAction(SpeechRecognize(true))
            } else {
                VoiceHelpDialog(
                    onConfirm = { localViewModel.submitAction(SpeechRecognize(it)) },
                    onDismiss = {
                        showVoiceHelpDialog = false
                    }
                )
            }
        }
    }
}