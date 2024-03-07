package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.OpenYandexMusic

@Composable
fun YandexMusicDialog(
    commonViewModel: CommonViewModel,
    onDismiss: () -> Unit
) = MusicDialog(
    commonViewModel = commonViewModel,
    stringRes = R.string.question_search_at_yandex_music,
    onConfirm = { commonViewModel.submitAction(OpenYandexMusic(it)) },
    onDismiss = onDismiss
)