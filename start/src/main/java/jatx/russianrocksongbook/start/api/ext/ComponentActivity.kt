package jatx.russianrocksongbook.start.api.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import jatx.russianrocksongbook.start.internal.viewmodel.StartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ComponentActivity.asyncInit() {
    GlobalScope.launch {
        withContext(Dispatchers.Main) {
            val startViewModel: StartViewModel by viewModels()
            startViewModel.asyncInit()
        }
    }
}