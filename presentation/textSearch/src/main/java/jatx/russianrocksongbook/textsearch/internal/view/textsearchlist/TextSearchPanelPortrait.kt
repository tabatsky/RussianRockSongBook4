package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.testing.SEARCH_BUTTON
import jatx.russianrocksongbook.testing.TEXT_FIELD_SEARCH_FOR
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel
import jatx.spinner.Spinner

@Composable
internal fun TextSearchPanelPortrait(
    searchFor: String,
    orderBy: TextSearchOrderBy,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onSearchForValueChange: (String) -> Unit,
    onOrderByValueChange: (TextSearchOrderBy) -> Unit,
    onSearchClick: () -> Unit
) {
    val textSearchViewModel = TextSearchViewModel.getInstance()

    val size1 = dimensionResource(id = R.dimen.search_button_size) * 0.5f
    val size2 = dimensionResource(id = R.dimen.search_button_size) * 0.75f
    val size3 = dimensionResource(id = R.dimen.search_button_size) * 1.25f
    val padding = dimensionResource(id = R.dimen.empty)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size3)
            .padding(all = padding)
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
        ) {
            TextField(
                value = searchFor,
                modifier = Modifier
                    .testTag(TEXT_FIELD_SEARCH_FOR)
                    .fillMaxWidth()
                    .height(size2 - padding)
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
                    .fillMaxWidth()
                    .height(size1)
                    .padding(padding),
                colorMain = theme.colorMain,
                colorBg = theme.colorBg,
                colorCommon = theme.colorCommon,
                fontSize = fontSizeTextSp,
                valueList = TextSearchOrderBy.entries.map { it.orderByRus }.toTypedArray(),
                initialPosition = orderBy.ordinal,
                onPositionChanged = { onOrderByValueChange(TextSearchOrderBy.entries[it]) },
                spinnerState = textSearchViewModel.spinnerStateOrderBy
            )
        }
        Box (
            modifier = Modifier
                .width(size3 - padding * 2)
                .height(size3 - padding * 2)
                .padding(padding)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .testTag(SEARCH_BUTTON)
                    .fillMaxSize(),
                shape = RoundedCornerShape(size3 * 0.1f),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults
                    .outlinedButtonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = theme.colorMain
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