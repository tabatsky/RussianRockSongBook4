package jatx.russianrocksongbook.addartist.viewmodel

import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddArtistStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val showUploadDialogForDir = MutableStateFlow(false)
    val uploadArtist = MutableStateFlow("")
    val uploadSongList = MutableStateFlow<List<Song>>(listOf())
}