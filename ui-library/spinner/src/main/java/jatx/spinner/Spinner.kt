package jatx.spinner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun Spinner(
    modifier: Modifier,
    colorLabel: Color,
    colorMain: Color,
    colorBg: Color,
    colorCommon: Color,
    testTag: String? = null,
    fontSize: TextUnit,
    valueList: Array<String>,
    initialPosition: Int,
    onPositionChanged: (Int) -> Unit,
    spinnerState: MutableState<SpinnerState> = rememberSaveable {
        mutableStateOf(SpinnerState(0, false))
    }
) {
    var theState by spinnerState

    fun setPosition(position: Int) {
        theState = theState.copy(position = position)
    }

    fun setExpanded(isExpanded: Boolean) {
        theState = theState.copy(isExpanded = isExpanded)
    }

    LaunchedEffect(initialPosition) {
        setPosition(initialPosition)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        val modifierWithTestTag = testTag?.let {
            Modifier.testTag(it)
        } ?: Modifier

        Button(
            modifier = modifierWithTestTag
                .fillMaxSize(),
            colors = ButtonDefaults
                .buttonColors(
                    backgroundColor = colorCommon,
                    contentColor = colorLabel
                ),
            onClick = {
                setExpanded(!theState.isExpanded)
            }
        ) {
            Text(
                text = valueList[theState.position],
                fontSize = fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            modifier = Modifier
                .background(colorMain),
            expanded = theState.isExpanded,
            onDismissRequest = { setExpanded(false) }) {
            valueList.forEachIndexed { index, string ->
                DropdownMenuItem(
                    onClick = {
                        setExpanded(false)
                        setPosition(index)
                        onPositionChanged(index)
                    }
                ) {
                    Text(
                        text = string,
                        color = colorBg
                    )
                }
            }
        }
    }
}

data class SpinnerState(
    val position: Int,
    val isExpanded: Boolean
)