package jatx.russianrocksongbook.commonview.stub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
fun ErrorSongListStub(
    fontSizeSp: TextUnit,
    theme: Theme
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.label_error_placeholder),
            textAlign = TextAlign.Center,
            fontSize = fontSizeSp,
            color = theme.colorMain
        )
    }
}
