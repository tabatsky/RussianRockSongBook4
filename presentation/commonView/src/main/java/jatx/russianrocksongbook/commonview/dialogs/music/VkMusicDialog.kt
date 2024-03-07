package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.OpenVkMusic

@Composable
fun VkMusicDialog(
    commonViewModel: CommonViewModel,
    onDismiss: () -> Unit
) = MusicDialog(
    commonViewModel = commonViewModel,
    stringRes = R.string.question_search_at_vk_music,
    onConfirm = { commonViewModel.submitAction(OpenVkMusic(it)) },
    onDismiss = onDismiss
)