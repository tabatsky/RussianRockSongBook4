package jatx.russianrocksongbook.addartist.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddArtistStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val showUploadDialogForDir = MutableStateFlow(false)
    val uploadArtist = MutableStateFlow("")
    val uploadSongList = MutableStateFlow<List<Song>>(listOf())
}