package jatx.russianrocksongbook.addsong.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddSongStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val showUploadDialogForSong = MutableStateFlow(false)
    val newSong: MutableStateFlow<Song?> = MutableStateFlow(null)
}