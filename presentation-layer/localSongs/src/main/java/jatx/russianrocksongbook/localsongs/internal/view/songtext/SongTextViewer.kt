package jatx.russianrocksongbook.localsongs.internal.view.songtext

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import com.dqt.libs.chorddroid.classes.ChordLibrary
import jatx.clickablewordstextview.api.ClickableWordsTextView
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.SONG_TEXT_VIEWER
import kotlinx.coroutines.launch

@Composable
internal fun SongTextViewer(
    song: Song,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (Word) -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()
    AndroidView(
        modifier = Modifier.testTag(SONG_TEXT_VIEWER),
        factory = { context ->
            ClickableWordsTextView(context)
        },
        update = { view ->
            view.text = song.text
            view.actualWordMappings = ChordLibrary.chordMappings
            view.actualWordSet = ChordLibrary.baseChords.keys
            view.setTextColor(theme.colorMain.toArgb())
            view.setBackgroundColor(theme.colorBg.toArgb())
            view.textSize = fontSizeTextSp.value
            view.typeface = Typeface.MONOSPACE
            localViewModel.viewModelScope.launch {
                view.wordFlow.collect {
                    onWordClick(it)
                }
            }
        }
    )
}