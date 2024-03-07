package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.cloud.DeleteFromCloudUseCase
import jatx.russianrocksongbook.domain.usecase.cloud.PagedSearchUseCase
import jatx.russianrocksongbook.domain.usecase.cloud.VoteUseCase
import jatx.russianrocksongbook.domain.usecase.local.AddSongFromCloudUseCase
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
class CloudViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val addSongFromCloudUseCase: AddSongFromCloudUseCase,
    val pagedSearchUseCase: PagedSearchUseCase,
    val voteUseCase: VoteUseCase,
    val deleteFromCloudUseCase: DeleteFromCloudUseCase
)