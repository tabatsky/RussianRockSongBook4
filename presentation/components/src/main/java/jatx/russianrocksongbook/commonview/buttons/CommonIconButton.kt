package jatx.russianrocksongbook.commonview.buttons

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource

@Composable
fun CommonIconButton(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    onClick: () -> Unit
) {
    val theModifier = testTag?.let {
        modifier.testTag(it)
    } ?: modifier
    IconButton(
        modifier = theModifier,
        onClick = onClick
    ) {
        Icon(painterResource(id = resId), "")
    }
}