package jatx.russianrocksongbook.commonsongtext.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import jatx.russianrocksongbook.domain.usecase.local.DeleteSongToTrashUseCase
import jatx.russianrocksongbook.domain.usecase.local.GetCountByArtistUseCase
import jatx.russianrocksongbook.domain.usecase.local.GetSongByArtistAndPositionUseCase
import jatx.russianrocksongbook.domain.usecase.local.UpdateSongUseCase
import javax.inject.Inject

@ViewModelScoped
class CommonSongTextViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val getCountByArtistUseCase: GetCountByArtistUseCase,
    val getSongByArtistAndPositionUseCase: GetSongByArtistAndPositionUseCase,
    val updateSongUseCase: UpdateSongUseCase,
    val deleteSongToTrashUseCase: DeleteSongToTrashUseCase
)