package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.spinner.Spinner
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.testing.ORIENTATION_SPINNER
import jatx.spinner.SpinnerState

@Composable
internal fun OrientationRow(
    modifier: Modifier,
    theme: Theme,
    fontSize: TextUnit,
    valueOrientation: MutableState<Orientation>,
    spinnerStateOrientation: MutableState<SpinnerState>,
    onPositionChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.orient_fix),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.orientation_list)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            colorLabel = colorBlack,
            colorMain = theme.colorMain,
            colorBg = theme.colorBg,
            colorCommon = theme.colorCommon,
            testTag = ORIENTATION_SPINNER,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = valueOrientation.value.ordinal,
            onPositionChanged = onPositionChanged,
            spinnerState = spinnerStateOrientation
        )
    }
}
