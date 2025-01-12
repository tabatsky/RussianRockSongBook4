package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.DarkTheme
import jatx.russianrocksongbook.domain.models.local.Song

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun CommonSongTextScreenImplPreviewLandscapeDark() {
    val artist = "Исполнитель"
    val position = 3
    val song = Song(
        artist = artist,
        title = "Название",
        text = "Текст текст\nТекст\nAm Em\nТекст\n"
    )
    val songCount = 10
    val editorText = remember { mutableStateOf(song.text) }

    DarkTheme {
        CommonSongTextScreenImplContent(
            artist = artist,
            position = position,
            song = song,
            songCount = songCount,
            currentSongPosition = position,
            editorText = editorText,
            submitAction = {},
            submitEffect = {}
        )
    }
}
