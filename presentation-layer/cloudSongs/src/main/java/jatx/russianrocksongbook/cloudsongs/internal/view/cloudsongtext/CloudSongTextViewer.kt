package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dqt.libs.chorddroid.classes.ChordLibrary
import jatx.clickablewordstextview.api.ClickableWordsTextView
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.testing.CLOUD_SONG_TEXT_VIEWER
import kotlinx.coroutines.launch

@Composable
internal fun CloudSongTextViewer(
    cloudSong: CloudSong,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onWordClick: (Word) -> Unit
) {
    val cloudViewModel: CloudViewModel = viewModel()
    AndroidView(
        modifier = Modifier.testTag(CLOUD_SONG_TEXT_VIEWER),
        factory = { context ->
            ClickableWordsTextView(context)
        },
        update = { view ->
            view.text = cloudSong.text
            view.actualWordMappings = ChordLibrary.chordMappings
            view.actualWordSet = ChordLibrary.baseChords.keys
            view.setTextColor(theme.colorMain.toArgb())
            view.setBackgroundColor(theme.colorBg.toArgb())
            view.textSize = fontSizeTextSp.value
            view.typeface = Typeface.MONOSPACE
            cloudViewModel.viewModelScope.launch {
                view.wordFlow.collect {
                    onWordClick(it)
                }
            }
        }
    )
}
