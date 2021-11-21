package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.model.preferences.Theme

@Composable
fun Spinner(
    modifier: Modifier,
    theme: Theme,
    fontSize: TextUnit,
    valueList: Array<String>,
    initialPosition: Int,
    onPositionChanged: (Int) -> Unit
) {
    var position by remember { mutableStateOf(initialPosition) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
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