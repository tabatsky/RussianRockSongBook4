package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.TextUnit
import com.dqt.libs.chorddroid.classes.ChordLibrary
import jatx.clickablewordstextcompose.api.ClickableWordText
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.testing.SONG_TEXT_VIEWER

@Composable
internal fun CommonSongTextViewer(
    song: Song,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (String) -> Unit
) {
    ClickableWordText(
        text = song.text,
        actualWordSet = ChordLibrary.baseChords.keys,
        actualWordMappings = ChordLibrary.chordMappings,
        modifier = Modifier.testTag(SONG_TEXT_VIEWER),
        colorMain = theme.colorMain,
        colorBg = theme.colorBg,
        fontSize = fontSizeTextSp,
        onWordClick = onWordClick
    )
}