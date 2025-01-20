package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.dqt.libs.chorddroid.classes.ChordLibrary
import jatx.clickablewordstextcompose.api.ClickableWordText
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongTextViewer(
    cloudSong: CloudSong,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (String) -> Unit
) {
    ClickableWordText(
        text = cloudSong.text,
        actualWordSet = ChordLibrary.baseChords.keys,
        actualWordMappings = ChordLibrary.chordMappings,
        modifier = Modifier,
        colorMain = theme.colorMain,
        colorBg = theme.colorBg,
        fontSize = fontSizeTextSp,
        onWordClick = onWordClick
    )
}
