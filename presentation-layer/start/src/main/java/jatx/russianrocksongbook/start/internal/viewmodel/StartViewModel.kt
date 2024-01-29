package jatx.russianrocksongbook.start.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    val startState = startStateHolder
        .startState.asStateFlow()

    val needShowStartScreen = settings.appWasUpdated

    private var skipAsyncInit = false

    private var asyncInitJob: Job? = null

    companion object {
        private const val key = "Start"

        @Composable
        fun getInstance(): StartViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<StartViewModel>()
            }
            storage[key]?.relaunchJobIfNecessary()
            return storage[key] as StartViewModel
        }
    }

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
                            updateStubProgress(it.first, it.second)
                        }
                        localRepoInitializer.deleteWrongSongs()
                        localRepoInitializer.deleteWrongArtists()
                        localRepoInitializer.patchWrongArtists()
                        localRepoInitializer.applySongPatches()
                        setAppWasUpdated(true)
                    }
                    withContext(Dispatchers.Main) {
                        selectScreen(ScreenVariant.SongList(settings.defaultArtist))
                        settings.confirmAppUpdate()
                    }
                }
            }
        }
    }

    private fun updateStubProgress(current: Int, total: Int) {
        startStateHolder.startState.update {
            it.copy(stubCurrentProgress = current, stubTotalProgress = total)
        }
    }
}