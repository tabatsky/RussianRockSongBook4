package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.testing.SEARCH_BUTTON
import jatx.russianrocksongbook.testing.TEXT_FIELD_SEARCH_FOR
import jatx.russianrocksongbook.textsearch.R
import jatx.spinner.Spinner
import jatx.spinner.SpinnerState

@Composable
internal fun TextSearchListPanelLandscape(
    searchFor: String,
    orderBy: TextSearchOrderBy,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onSearchForValueChange: (String) -> Unit,
    onOrderByValueChange: (TextSearchOrderBy) -> Unit,
    onSearchClick: () -> Unit,
    spinnerStateOrderBy: MutableState<SpinnerState>
) {
    val size = dimensionResource(id = R.dimen.search_button_size) * 0.75f
    val padding = dimensionResource(id = R.dimen.empty)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .padding(start = padding, end = padding)
    ) {
        TextField(
            value = searchFor,
            modifier = Modifier
                .testTag(TEXT_FIELD_SEARCH_FOR)
                .weight(1.0f)
                .fillMaxHeight()
                .padding(padding),
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontSize = fontSizeTextSp
            ),
            onValueChange = onSearchForValueChange
        )
        Spinner(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(padding),
            colorLabel = colorBlack,
            colorMain = theme.colorMain,
            colorBg = theme.colorBg,
            colorCommon = theme.colorCommon,
            fontSize = fontSizeTextSp,
            valueList = TextSearchOrderBy.entries.map { it.orderByRus }.toTypedArray(),
            initialPosition = orderBy.ordinal,
            onPositionChanged = { onOrderByValueChange(TextSearchOrderBy.entries[it]) },
            spinnerState = spinnerStateOrderBy
        )
        Box (
            modifier = Modifier
                .width(size)
                .height(size)
                .padding(padding)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .testTag(SEARCH_BUTTON)
                    .fillMaxSize(),
                shape = RoundedCornerShape(size * 0.1f),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults
                    .outlinedButtonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = colorBlack
                    ),
                onClick = onSearchClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cloud_search),
                    contentDescription = ""
                )
            }
        }
    }
}