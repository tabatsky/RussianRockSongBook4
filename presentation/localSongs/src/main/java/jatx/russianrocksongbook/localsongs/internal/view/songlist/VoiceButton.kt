package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.localsongs.R

@Composable
internal fun VoiceButton(modifier: Modifier, theme: Theme, onClick: () -> Unit) {
    FloatingActionButton(
        modifier = modifier,
        backgroundColor = theme.colorCommon,
        contentColor = colorBlack,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_voice),
            contentDescription = "",
            modifier = Modifier
                .size(80.dp)
                .padding(20.dp)
        )
    }
}