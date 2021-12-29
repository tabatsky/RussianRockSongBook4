package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.database.dbinit.*
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class StartViewModel @Inject constructor(
    startViewModelDeps: StartViewModelDeps,
    private val startStateHolder: StartStateHolder
): MvvmViewModel(
    startViewModelDeps,
    startStateHolder.commonStateHolder
) {

    val songRepository = startViewModelDeps.songRepository

    val stubCurrentProgress = startStateHolder
        .stubCurrentProgress.asStateFlow()
    val stubTotalProgress = startStateHolder
        .stubTotalProgress.asStateFlow()

    suspend fun asyncInit() {
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

    fun updateStubProgress(current: Int, total: Int) {
        startStateHolder.stubCurrentProgress.value = current
        startStateHolder.stubTotalProgress.value = total
    }
}