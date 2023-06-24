package jatx.russianrocksongbook.start.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.navigation.CurrentScreenVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class StartViewModel @Inject constructor(
    private val startStateHolder: StartStateHolder,
    startViewModelDeps: StartViewModelDeps
): CommonViewModel(
    startStateHolder.commonStateHolder,
    startViewModelDeps.commonViewModelDeps
) {
    private val localRepoInitializer =
        startViewModelDeps.localRepoInitializer

    val stubCurrentProgress = startStateHolder
        .stubCurrentProgress.asStateFlow()
    val stubTotalProgress = startStateHolder
        .stubTotalProgress.asStateFlow()

    var asyncInitDone = false

    private var asyncInitJob: Job? = null

    companion object {
        private const val key = "Start"

        @Composable
        fun getInstance(): StartViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<StartViewModel>()
            }
            return storage[key] as StartViewModel
        }
    }

    fun asyncInit() {
        if (!asyncInitDone) {
            Log.e("async", "init")
            asyncInitJob?.let {
                if (!it.isCancelled) it.cancel()
            }
            asyncInitJob = viewModelScope.launch {
                Log.e("async", "init2")
                withContext(Dispatchers.IO) {
                    Log.e("async", "init3")
                    if (settings.appWasUpdated) {
                        localRepoInitializer.fillDbFromJSON().collect {
                            updateStubProgress(it.first, it.second)
                        }
                        localRepoInitializer.deleteWrongSongs()
                        localRepoInitializer.deleteWrongArtists()
                        localRepoInitializer.patchWrongArtists()
                        localRepoInitializer.applySongPatches()
                        setAppWasUpdated(true)
                    }
                    withContext(Dispatchers.Main) {
                        settings.confirmAppUpdate()
                        selectScreen(CurrentScreenVariant.SONG_LIST(settings.defaultArtist))
                        asyncInitDone = true
                        Log.e("async", "init4")
                    }
                }
            }
        }
    }

    private fun updateStubProgress(current: Int, total: Int) {
        startStateHolder.stubCurrentProgress.value = current
        startStateHolder.stubTotalProgress.value = total
    }
}