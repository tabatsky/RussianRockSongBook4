package jatx.russianrocksongbook.addartist.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.AddSongListToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.InsertReplaceUserSongsUseCase
import jatx.russianrocksongbook.domain.repository.FileSystemRepository
import jatx.russianrocksongbook.viewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class AddArtistViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val insertReplaceUserSongsUseCase: InsertReplaceUserSongsUseCase,
    val addSongListToCloudUseCase: AddSongListToCloudUseCase,
    val fileSystemRepository: FileSystemRepository
)