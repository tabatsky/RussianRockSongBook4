package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.DarkTheme
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant

@Preview
@Composable
fun CommonSongTextScreenImplPreviewPortraitDark() {
    val artist = "Исполнитель"
    val position = 3
    val song = Song(
        artist = artist,
        title = "Название",
        text = "Текст текст\nТекст\nAm Em\nТекст\n"
    )
    val editorText = remember { mutableStateOf(song.text) }

    DarkTheme {
        CommonSongTextScreenImplContent(
            artist = artist,
            position = position,
            song = song,
            currentSongPosition = position,
            isAutoPlayMode = false,
            isEditorMode = false,
            isUploadButtonEnabled = true,
            editorText = editorText,
            scrollSpeed = 1.0f,
            listenToMusicVariant = ListenToMusicVariant.YANDEX_AND_YOUTUBE,
            vkMusicDontAsk = false,
            yandexMusicDontAsk = false,
            youtubeMusicDontAsk = false,
            submitAction = {},
            submitEffect = {}
        )
    }
}
