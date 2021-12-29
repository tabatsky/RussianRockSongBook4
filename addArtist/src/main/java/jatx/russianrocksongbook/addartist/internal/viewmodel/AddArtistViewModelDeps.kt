package jatx.russianrocksongbook.addartist.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.usecase.AddSongListToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.InsertReplaceUserSongsUseCase
import jatx.russianrocksongbook.domain.repository.FileSystemRepository
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddArtistViewModelDeps @Inject constructor(): ViewModelDeps() {
    @Inject
    lateinit var insertReplaceUserSongsUseCase: InsertReplaceUserSongsUseCase

    @Inject
    lateinit var addSongListToCloudUseCase: AddSongListToCloudUseCase

    @Inject
    lateinit var fileSystemRepository: FileSystemRepository
}