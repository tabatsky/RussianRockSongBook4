package jatx.russianrocksongbook.start.api.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import jatx.russianrocksongbook.start.internal.viewmodel.StartViewModel

fun ComponentActivity.asyncInit() {
    val startViewModel: StartViewModel by viewModels()
    startViewModel.asyncInit()
}