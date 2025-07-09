package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.LightTheme
import jatx.russianrocksongbook.domain.models.local.Song

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun CommonSongTextScreenImplPreviewLandscapeLight() {
    val artist = "Исполнитель"
    val position = 3
    val song = Song(
        artist = artist,
        title = "Название",
        text = "Текст текст\nТекст\nAm Em\nТекст\n"
    )
    val songCount = 10
    val editorText = remember { mutableStateOf(song.text) }

    LightTheme {
        CommonSongTextScreenImplContent(
            position = position,
            randomKey = 0,
            song = song,
            songCount = songCount,
            lastRandomKey = 0,
            currentSongPosition = position,
            editorText = editorText,
            submitAction = {},
            submitEffect = {}
        )
    }
}