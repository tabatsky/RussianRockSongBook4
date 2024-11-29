package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextViewModelDeps
import jatx.russianrocksongbook.domain.usecase.local.*
import javax.inject.Inject

@ViewModelScoped
class LocalViewModelDeps @Inject constructor(
    val commonSongTextViewModelDeps: CommonSongTextViewModelDeps,
    val getSongsByArtistUseCase: GetSongsByArtistUseCase,
    val getArtistsUseCase: GetArtistsUseCase
)