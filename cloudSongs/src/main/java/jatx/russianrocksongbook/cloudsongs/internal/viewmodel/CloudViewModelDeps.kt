package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.usecase.*
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@ActivityRetainedScoped
internal class CloudViewModelDeps @Inject constructor(): ViewModelDeps() {
    @Inject
    lateinit var addSongFromCloudUseCase: AddSongFromCloudUseCase

    @Inject
    lateinit var pagedSearchUseCase: PagedSearchUseCase

    @Inject
    lateinit var voteUseCase: VoteUseCase

    @Inject
    lateinit var deleteFromCloudUseCase: DeleteFromCloudUseCase

    @Inject
    lateinit var addWarningCloudUseCase: AddWarningCloudUseCase
}