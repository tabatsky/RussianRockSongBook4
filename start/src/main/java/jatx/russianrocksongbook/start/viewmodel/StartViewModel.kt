package jatx.russianrocksongbook.start.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.database.api.*
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val startStateHolder: StartStateHolder
): MvvmViewModel(
    viewModelParam,
    startStateHolder.commonStateHolder
) {

    val stubCurrentProgress = startStateHolder
        .stubCurrentProgress.asStateFlow()
    val stubTotalProgress = startStateHolder
        .stubTotalProgress.asStateFlow()

    suspend fun asyncInit() {
        if (settings.appWasUpdated) {
            withContext(Dispatchers.IO) {
                fillDbFromJSON(songRepo, context) { current, total ->
                    updateStubProgress(current, total)
                }
                deleteWrongSongs(songRepo)
                deleteWrongArtists(songRepo)
                patchWrongArtists(songRepo)
                applySongPatches(songRepo)
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