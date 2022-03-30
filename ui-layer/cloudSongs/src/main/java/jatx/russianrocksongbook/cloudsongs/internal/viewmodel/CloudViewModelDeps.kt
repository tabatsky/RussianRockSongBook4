package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.*
import jatx.russianrocksongbook.viewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class CloudViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val addSongFromCloudUseCase: AddSongFromCloudUseCase,
    val pagedSearchUseCase: PagedSearchUseCase,
    val voteUseCase: VoteUseCase,
    val deleteFromCloudUseCase: DeleteFromCloudUseCase,
    val addWarningCloudUseCase: AddWarningCloudUseCase
)