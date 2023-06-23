package jatx.russianrocksongbook.commonview.spinner

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
fun Spinner(
    modifier: Modifier,
    theme: Theme,
    testTag: String? = null,
    fontSize: TextUnit,
    valueList: Array<String>,
    initialPosition: Int,
    onPositionChanged: (Int) -> Unit,
    positionState: MutableState<Int> = mutableStateOf(0),
    isExpandedState: MutableState<Boolean> = mutableStateOf(false)
) {
    var position by positionState
    var isExpanded by isExpandedState

    position = initialPosition

    Log.e("initialPosition", initialPosition.toString())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        val modifier = testTag?.let {
            Modifier.testTag(it)
        } ?: Modifier

        Button(
            modifier = modifier
                .fillMaxSize(),
            colors = ButtonDefaults
                .buttonColors(
                    backgroundColor = theme.colorCommon,
                    contentColor = theme.colorMain
                ),
            onClick = {
                isExpanded = !isExpanded
            }
        ) {
            Text(
                text = valueList[position],
                fontSize = fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            modifier = Modifier
                .background(theme.colorMain),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            valueList.forEachIndexed { index, string ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        position = index
                        onPositionChanged(index)
                    }
                ) {
                    Text(
                        text = string,
                        color = theme.colorBg
                    )
                }
            }
        }
    }
}