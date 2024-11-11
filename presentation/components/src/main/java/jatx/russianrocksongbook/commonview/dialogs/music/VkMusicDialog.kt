package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.OpenVkMusic
import jatx.russianrocksongbook.commonviewmodel.UIAction

@Composable
fun VkMusicDialog(
    submitAction: (UIAction) -> Unit,
    onDismiss: () -> Unit
) = MusicDialog(
    stringRes = R.string.question_search_at_vk_music,
    onConfirm = { submitAction(OpenVkMusic(it)) },
    onDismiss = onDismiss
)