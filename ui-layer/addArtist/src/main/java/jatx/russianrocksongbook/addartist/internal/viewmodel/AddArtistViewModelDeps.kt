package jatx.russianrocksongbook.addartist.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.cloud.AddSongListToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.local.InsertReplaceUserSongsUseCase
import jatx.russianrocksongbook.domain.repository.filesystem.FileSystemRepository
import jatx.russianrocksongbook.viewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class AddArtistViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val insertReplaceUserSongsUseCase: InsertReplaceUserSongsUseCase,
    val addSongListToCloudUseCase: AddSongListToCloudUseCase,
    val fileSystemRepository: FileSystemRepository
)