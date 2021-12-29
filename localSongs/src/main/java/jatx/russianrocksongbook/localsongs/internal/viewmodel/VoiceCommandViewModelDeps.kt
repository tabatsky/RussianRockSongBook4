package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.usecase.GetArtistsAsListUseCase
import jatx.russianrocksongbook.domain.usecase.GetSongsByVoiceSearchUseCase
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@ActivityRetainedScoped
internal class VoiceCommandViewModelDeps @Inject constructor(): LocalViewModelDeps() {
    @Inject
    lateinit var getArtistsAsListUseCase: GetArtistsAsListUseCase

    @Inject
    lateinit var getSongsByVoiceSearchUseCase: GetSongsByVoiceSearchUseCase
}