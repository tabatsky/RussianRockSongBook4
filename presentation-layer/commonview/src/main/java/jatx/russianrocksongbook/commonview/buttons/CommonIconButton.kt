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
    testTag: String? = null,
    onClick: () -> Unit
) {
    val modifier = testTag?.let {
        Modifier.testTag(it)
    } ?: Modifier
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(painterResource(id = resId), "")
    }
}