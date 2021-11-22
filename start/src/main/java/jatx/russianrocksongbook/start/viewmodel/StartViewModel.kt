package jatx.russianrocksongbook.start.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.model.db.util.applySongPatches
import jatx.russianrocksongbook.model.db.util.deleteWrongArtists
import jatx.russianrocksongbook.model.db.util.deleteWrongSongs
import jatx.russianrocksongbook.model.db.util.fillDbFromJSON
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
    private val startScreenStateHolder: StartScreenStateHolder
): MvvmViewModel(
    viewModelParam,
    startScreenStateHolder.screenStateHolder
) {

    val stubCurrentProgress = startScreenStateHolder
        .stubCurrentProgress.asStateFlow()
    val stubTotalProgress = startScreenStateHolder
        .stubTotalProgress.asStateFlow()

    suspend fun asyncInit() {
        if (settings.appWasUpdated) {
            withContext(Dispatchers.IO) {
                fillDbFromJSON(songRepo, context) { current, total ->
                    updateStubProgress(current, total)
                }
                deleteWrongSongs(songRepo)
                deleteWrongArtists(songRepo)
                applySongPatches(songRepo)
            }
            setAppWasUpdated(true)
        }
        settings.confirmAppUpdate()
        selectScreen(CurrentScreenVariant.SONG_LIST)
    }

    fun updateStubProgress(current: Int, total: Int) {
        startScreenStateHolder.stubCurrentProgress.value = current
        startScreenStateHolder.stubTotalProgress.value = total
    }
}