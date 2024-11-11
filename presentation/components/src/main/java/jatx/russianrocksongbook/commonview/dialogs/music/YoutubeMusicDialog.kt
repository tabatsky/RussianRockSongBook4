package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.OpenYoutubeMusic
import jatx.russianrocksongbook.commonviewmodel.UIAction

@Composable
fun YoutubeMusicDialog(
    submitAction: (UIAction) -> Unit,
    onDismiss: () -> Unit
) = MusicDialog(
    stringRes = R.string.question_search_at_youtube_music,
    onConfirm = { submitAction(OpenYoutubeMusic(it)) },
    onDismiss = onDismiss
)