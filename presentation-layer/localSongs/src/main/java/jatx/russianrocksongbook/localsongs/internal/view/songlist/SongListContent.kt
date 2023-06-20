package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.ext.crop
import jatx.russianrocksongbook.localsongs.internal.view.dialogs.VoiceHelpDialog
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.whatsnewdialog.api.view.WhatsNewDialog

private const val MAX_ARTIST_LENGTH_LANDSCAPE = 12
private const val MAX_ARTIST_LENGTH_PORTRAIT = 15

@Composable
internal fun SongListContent(
    openDrawer: () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme
    val artist by localViewModel.currentArtist.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        val isPortrait = W < H
        var isLastOrientationPortrait by remember { mutableStateOf(isPortrait) }

        LaunchedEffect(isPortrait) {
            if (isPortrait != isLastOrientationPortrait) {
                isLastOrientationPortrait = isPortrait
                localViewModel.updateNeedScroll(true)
            }
        }

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

        var showVoiceHelpDialog by remember { mutableStateOf(false) }
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
                localViewModel.speechRecognize(true)
            } else {
                VoiceHelpDialog(
                    onConfirm = localViewModel::speechRecognize,
                    onDismiss = {
                        showVoiceHelpDialog = false
                    }
                )
            }
        }
    }
}