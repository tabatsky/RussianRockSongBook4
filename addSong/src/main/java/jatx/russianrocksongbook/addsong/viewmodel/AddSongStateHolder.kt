package jatx.russianrocksongbook.addsong.viewmodel

import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val showUploadDialogForSong = MutableStateFlow(false)
    val newSong: MutableStateFlow<Song?> = MutableStateFlow(null)
}