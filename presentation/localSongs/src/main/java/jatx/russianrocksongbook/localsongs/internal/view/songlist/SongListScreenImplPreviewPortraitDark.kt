package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.DarkTheme
import jatx.russianrocksongbook.domain.models.local.Song

@Preview
@Composable
fun SongListScreenImplPreviewPortraitDark() {
    val artistList = (1..30)
        .map { "Исполнитель $it" }
    val songList = (1..30)
        .map {
            Song(
                artist = "Исполнитель 1",
                title = "Название $it"
            )
        }

    DarkTheme {
        SongListScreenImplContent(
            artist = "Исполнитель 1",
            isBackFromSomeScreen = false,
            songTitleToPass = null,
            artistList = artistList,
            currentArtist = "Исполнитель 1",
            songList = songList,
            songListScrollPosition = 0,
            songListNeedScroll = false,
            menuExpandedArtistGroup = "",
            menuScrollPosition = 0,
            voiceHelpDontAsk = false,
            submitAction = {}
        )
    }
}