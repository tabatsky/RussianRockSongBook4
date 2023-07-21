package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel

@Composable
fun YoutubeMusicDialog(
    commonViewModel: CommonViewModel,
    onDismiss: () -> Unit
) = MusicDialog(
    commonViewModel = commonViewModel,
    stringRes = R.string.question_search_at_youtube_music,
    onConfirm = commonViewModel::openYoutubeMusic,
    onDismiss = onDismiss
)