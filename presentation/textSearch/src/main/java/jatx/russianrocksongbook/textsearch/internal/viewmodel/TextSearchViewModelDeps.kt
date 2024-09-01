package jatx.russianrocksongbook.textsearch.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import jatx.russianrocksongbook.domain.usecase.local.DeleteSongToTrashUseCase
import jatx.russianrocksongbook.domain.usecase.local.GetSongsByTextSearchUseCase
import jatx.russianrocksongbook.domain.usecase.local.UpdateSongUseCase
import javax.inject.Inject

@ViewModelScoped
class CloudViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val getSongsByTextSearchUseCase: GetSongsByTextSearchUseCase,
    val updateSongUseCase: UpdateSongUseCase,
    val deleteSongToTrashUseCase: DeleteSongToTrashUseCase
)
