package jatx.russianrocksongbook.addartist.viewmodel

import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddArtistScreenStateHolder @Inject constructor(
    val screenStateHolder: ScreenStateHolder
) {
    val showUploadDialogForDir = MutableStateFlow(false)
    val uploadArtist = MutableStateFlow("")
    val uploadSongList = MutableStateFlow<List<Song>>(listOf())
}