package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.cloud.AddSongToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.local.*
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class LocalViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val getSongsByArtistUseCase: GetSongsByArtistUseCase,
    val getCountByArtistUseCase: GetCountByArtistUseCase,
    val getSongByArtistAndPositionUseCase: GetSongByArtistAndPositionUseCase,
    val updateSongUseCase: UpdateSongUseCase,
    val deleteSongToTrashUseCase: DeleteSongToTrashUseCase,
    val addSongToCloudUseCase: AddSongToCloudUseCase,
    val getArtistsUseCase: GetArtistsUseCase
)