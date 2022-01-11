package jatx.russianrocksongbook.start.internal.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.database.dbinit.*
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import kotlinx.coroutines.Dispatchers
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
    private val songRepository =
        startViewModelDeps.localRepository

    val stubCurrentProgress = startStateHolder
        .stubCurrentProgress.asStateFlow()
    val stubTotalProgress = startStateHolder
        .stubTotalProgress.asStateFlow()

    fun asyncInit() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                if (settings.appWasUpdated) {
                    withContext(Dispatchers.IO) {
                        fillDbFromJSON(songRepository, context) { current, total ->
                            updateStubProgress(current, total)
                        }
                        deleteWrongSongs(songRepository)
                        deleteWrongArtists(songRepository)
                        patchWrongArtists(songRepository)
                        applySongPatches(songRepository)
                    }
                    setAppWasUpdated(true)
                }
                settings.confirmAppUpdate()
                selectScreen(CurrentScreenVariant.SONG_LIST)
            }
        }
    }

    private fun updateStubProgress(current: Int, total: Int) {
        startStateHolder.stubCurrentProgress.value = current
        startStateHolder.stubTotalProgress.value = total
    }
}