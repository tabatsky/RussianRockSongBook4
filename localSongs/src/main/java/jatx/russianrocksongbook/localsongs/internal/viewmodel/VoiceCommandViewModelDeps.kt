package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.GetArtistsAsListUseCase
import jatx.russianrocksongbook.domain.usecase.GetSongsByVoiceSearchUseCase
import javax.inject.Inject

@ViewModelScoped
internal class VoiceCommandViewModelDeps @Inject constructor(
    val localViewModelDeps: LocalViewModelDeps,
    val getArtistsAsListUseCase: GetArtistsAsListUseCase,
    val getSongsByVoiceSearchUseCase: GetSongsByVoiceSearchUseCase
)