package jatx.russianrocksongbook.start.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.start.internal.viewmodel.AsyncInit
import jatx.russianrocksongbook.start.internal.viewmodel.StartViewModel

@Composable
internal fun StartScreenImpl() {
    val startViewModel = StartViewModel.getInstance()

    LaunchedEffect(Unit) {
        startViewModel.submitAction(AsyncInit)
    }

    val startState by startViewModel.startStateFlow.collectAsState()

    val currentProgress = startState.currentProgress
    val totalProgress = startState.totalProgress

    val needShowStartScreen = startViewModel.needShowStartScreen

    StartScreenImplContent(
        currentProgress = currentProgress,
        totalProgress = totalProgress,
        needShowStartScreen = needShowStartScreen
    )
}