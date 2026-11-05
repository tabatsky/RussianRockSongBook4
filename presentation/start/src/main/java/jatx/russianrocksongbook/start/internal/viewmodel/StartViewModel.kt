package jatx.russianrocksongbook.start.internal.viewmodel

import jatx.russianrocksongbook.commonappstate.AppStateHolder
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.navigation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class StartViewModel @Inject constructor(
    appStateHolder: AppStateHolder,
    startViewModelDeps: StartViewModelDeps
): CommonViewModel(
    appStateHolder,
    startViewModelDeps.commonViewModelDeps
) {
    companion object {
        private const val key = "Start"

        @Composable
        fun getInstance(): StartViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<StartViewModel>()
            }
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as StartViewModel
        }
    }

    private val localRepoInitializer =
        startViewModelDeps.localRepoInitializer

    val startStateFlow = appStateHolder.stateFlow(StartStateKey)

    val needShowStartScreen = settings.appWasUpdated

    private var skipAsyncInit = false

    private var asyncInitJob: Job? = null

    override fun handleAction(action: UIAction) {
        when (action) {
            is AsyncInit -> asyncInit()
            else -> super.handleAction(action)
        }
    }

    private fun asyncInit() {
        if (!skipAsyncInit) {
            skipAsyncInit = true
            asyncInitJob?.let {
                if (!it.isCancelled) it.cancel()
            }
            asyncInitJob = viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    if (settings.appWasUpdated) {
                        localRepoInitializer.fillDbFromJSONResources().collect {
                            updateProgress(it.first, it.second)
                        }
                        localRepoInitializer.deleteWrongSongs()
                        localRepoInitializer.deleteWrongArtists()
                        localRepoInitializer.patchWrongArtists()
                        localRepoInitializer.applySongPatches()
                        setAppWasUpdated(true)
                    }
                    withContext(Dispatchers.Main) {
                        selectScreen(SongListScreenVariant(settings.defaultArtist))
                        settings.confirmAppUpdate()
                    }
                }
            }
        }
    }

    private fun updateProgress(current: Int, total: Int) {
        appStateHolder.updateState(StartStateKey) {
            it.copy(currentProgress = current, totalProgress = total)
        }
    }
}