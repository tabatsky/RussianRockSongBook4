/*
 * Copyright 2021 Evgeny Tabatsky
 *
 * Based on following code:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/AppBar.kt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jatx.sideappbar

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun SideAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleTestTag: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable ColumnScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    appBarWidth: Dp = 72.dp,
    elevation: Dp = AppBarDefaults.SideAppBarElevation
) {
    val offset = (TextMaxWidth - appBarWidth) / 2

    SideAppBar(
        backgroundColor,
        contentColor,
        appBarWidth,
        elevation,
        AppBarDefaults.ContentPadding,
        RectangleShape,
        modifier
    ) {
        if (navigationIcon == null) {
            Spacer(TitleInsetWithoutIcon)
        } else {
            Column(TitleIconModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                    content = navigationIcon
                )
            }
        }

        Column(
            Modifier
                .width(appBarWidth)
                .weight(1.0f)
        ) {
            title?.let {
                val scope = rememberCoroutineScope()
                val state = rememberLazyListState()
                LazyRow(
                    modifier = Modifier
                        .width(appBarWidth)
                        .fillMaxHeight(),
                    state = state
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .rotate(-90f)
                                .height(appBarWidth)
                                .offset(x = -offset, y = -offset),
                            verticalArrangement = Arrangement.Center
                        ) {
                            ProvideTextStyle(value = MaterialTheme.typography.h6) {
                                CompositionLocalProvider(
                                    LocalContentAlpha provides ContentAlpha.high,
                                ) {
                                    Text(
                                        text = it,
                                        modifier = titleTestTag?.let { testTag ->
                                            Modifier
                                                .width(TextMaxWidth)
                                                .testTag(testTag)
                                        } ?: Modifier.width(TextMaxWidth),
                                        softWrap = false,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
                state.disableScrolling(scope)
            }
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Column(
                Modifier.wrapContentHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = actions
            )
        }
    }
}

private fun LazyListState.disableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = MutatePriority.PreventUserInput) {
            awaitCancellation()
        }
    }
}

@Composable
fun SideAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    appBarWidth: Dp,
    elevation: Dp = AppBarDefaults.SideAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
    content: @Composable ColumnScope.() -> Unit
) {
    SideAppBar(
        backgroundColor,
        contentColor,
        appBarWidth,
        elevation,
        contentPadding,
        RectangleShape,
        modifier = modifier,
        content = content
    )
}


object AppBarDefaults {
    // TODO: clarify elevation in surface mapping - spec says 0.dp but it appears to have an
    //  elevation overlay applied in dark theme examples.
    /**
     * Default elevation used for [TopAppBar].
     */
    val SideAppBarElevation = 4.dp

    /**
     * Default padding used for [TopAppBar] and [BottomAppBar].
     */
    val ContentPadding = PaddingValues(
        top = AppBarVerticalPadding,
        bottom = AppBarVerticalPadding
    )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun square(x: Float) = x * x

@Suppress("NOTHING_TO_INLINE")
internal inline fun calculateCutoutCircleYIntercept(
    cutoutRadius: Float,
    verticalOffset: Float
): Float {
    return -sqrt(square(cutoutRadius) - square(verticalOffset))
}

@Suppress("UnnecessaryVariable")
internal fun calculateRoundedEdgeIntercept(
    controlPointX: Float,
    verticalOffset: Float,
    radius: Float
): Pair<Float, Float> {
    val a = controlPointX
    val b = verticalOffset
    val r = radius

    // expands to a2b2r2 + b4r2 - b2r4
    val discriminant = square(b) * square(r) * (square(a) + square(b) - square(r))
    val divisor = square(a) + square(b)
    // the '-b' part of the quadratic solution
    val bCoefficient = a * square(r)

    // Two solutions for the x coordinate relative to the midpoint of the circle
    val xSolutionA = (bCoefficient - sqrt(discriminant)) / divisor
    val xSolutionB = (bCoefficient + sqrt(discriminant)) / divisor

    // Get y coordinate from r2 = x2 + y2 -> y2 = r2 - x2
    val ySolutionA = sqrt(square(r) - square(xSolutionA))
    val ySolutionB = sqrt(square(r) - square(xSolutionB))

    // If the vertical offset is 0, the vertical center of the circle lines up with the top edge of
    // the bottom app bar, so both solutions are identical.
    // If the vertical offset is not 0, there are two distinct solutions: one that will meet in the
    // top half of the circle, and one that will meet in the bottom half of the circle. As the app
    // bar is always on the bottom edge of the circle, we are always interested in the bottom half
    // solution. To calculate which is which, it depends on whether the vertical offset is positive
    // or negative.
    val (xSolution, ySolution) = if (b > 0) {
        // When the offset is positive, the top edge of the app bar is below the center of the
        // circle. The largest solution will be the one closest to the bottom of the circle, so we
        // pick that.
        if (ySolutionA > ySolutionB) xSolutionA to ySolutionA else xSolutionB to ySolutionB
    } else {
        // When the offset is negative, the top edge of the app bar is above the center of the
        // circle. The smallest solution will be the one closest to the top of the circle, so we
        // pick that.
        if (ySolutionA < ySolutionB) xSolutionA to ySolutionA else xSolutionB to ySolutionB
    }

    // If the calculated x coordinate is further away from the origin than the control point, the
    // curve will fold back on itself. In this scenario, we actually join the circle above the
    // center, so invert the y coordinate.
    val adjustedYSolution = if (xSolution < controlPointX) -ySolution else ySolution
    return xSolution to adjustedYSolution
}

/**
 * An empty App Bar that expands to the parent's width. The default [LocalContentAlpha] is
 * [ContentAlpha.medium].
 *
 * For an App Bar that follows Material spec guidelines to be placed on the top of the screen, see
 * [TopAppBar].
 */
@Composable
private fun SideAppBar(
    backgroundColor: Color,
    contentColor: Color,
    appBarWidth: Dp,
    elevation: Dp,
    contentPadding: PaddingValues,
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = shape,
        modifier = modifier
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(contentPadding)
                    .width(appBarWidth),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

private val TextMaxWidth = 1000.dp
//private val AppBarWidth = 80.dp
// TODO: this should probably be part of the touch target of the start and end icons, clarify this
private val AppBarVerticalPadding = 4.dp
// Start inset for the title when there is no navigation icon provided
private val TitleInsetWithoutIcon = Modifier.height(16.dp - AppBarVerticalPadding)
// Start inset for the title when there is a navigation icon provided
private val TitleIconModifier = Modifier
    .fillMaxWidth()
    .height(64.dp - AppBarVerticalPadding)

