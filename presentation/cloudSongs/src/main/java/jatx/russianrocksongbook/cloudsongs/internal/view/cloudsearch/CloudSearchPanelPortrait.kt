package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.spinner.Spinner
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.testing.SEARCH_BUTTON
import jatx.russianrocksongbook.testing.TEXT_FIELD_SEARCH_FOR
import jatx.spinner.SpinnerState

@Composable
internal fun CloudSearchPanelPortrait(
    searchFor: String,
    orderBy: CloudSearchOrderBy,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onSearchForValueChange: (String) -> Unit,
    onOrderByValueChange: (CloudSearchOrderBy) -> Unit,
    onSearchClick: () -> Unit,
    spinnerStateOrderBy: MutableState<SpinnerState>
) {
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
                colorLabel = colorBlack,
                colorMain = theme.colorMain,
                colorBg = theme.colorBg,
                colorCommon = theme.colorCommon,
                fontSize = fontSizeTextSp,
                valueList = CloudSearchOrderBy.entries.map { it.orderByRus }.toTypedArray(),
                initialPosition = orderBy.ordinal,
                onPositionChanged = { onOrderByValueChange(CloudSearchOrderBy.entries[it]) },
                spinnerState = spinnerStateOrderBy
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