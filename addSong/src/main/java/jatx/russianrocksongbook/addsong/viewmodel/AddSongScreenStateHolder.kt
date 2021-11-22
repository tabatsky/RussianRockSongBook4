package jatx.russianrocksongbook.addsong.viewmodel

import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongScreenStateHolder @Inject constructor(
    val screenStateHolder: ScreenStateHolder
) {
    val showUploadDialogForSong = MutableStateFlow(false)
    val newSong: MutableStateFlow<Song?> = MutableStateFlow(null)
}