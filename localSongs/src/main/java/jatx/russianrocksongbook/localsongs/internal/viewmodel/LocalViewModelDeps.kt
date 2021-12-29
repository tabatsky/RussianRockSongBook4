package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.usecase.*
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@ActivityRetainedScoped
internal open class LocalViewModelDeps @Inject constructor(): ViewModelDeps() {
    @Inject
    lateinit var getSongsByArtistUseCase: GetSongsByArtistUseCase

    @Inject
    lateinit var getCountByArtistUseCase: GetCountByArtistUseCase

    @Inject
    lateinit var getSongByArtistAndPositionUseCase: GetSongByArtistAndPositionUseCase

    @Inject
    lateinit var updateSongUseCase: UpdateSongUseCase

    @Inject
    lateinit var deleteSongToTrashUseCase: DeleteSongToTrashUseCase

    @Inject
    lateinit var addWarningLocalUseCase: AddWarningLocalUseCase

    @Inject
    lateinit var addSongToCloudUseCase: AddSongToCloudUseCase
}