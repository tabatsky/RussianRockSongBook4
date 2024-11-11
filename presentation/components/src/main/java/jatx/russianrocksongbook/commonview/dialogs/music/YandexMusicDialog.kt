package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.OpenYandexMusic
import jatx.russianrocksongbook.commonviewmodel.UIAction

@Composable
fun YandexMusicDialog(
    submitAction: (UIAction) -> Unit,
    onDismiss: () -> Unit
) = MusicDialog(
    stringRes = R.string.question_search_at_yandex_music,
    onConfirm = { submitAction(OpenYandexMusic(it)) },
    onDismiss = onDismiss
)