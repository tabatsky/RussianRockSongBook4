package jatx.russianrocksongbook.addsong.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.usecase.AddSongToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.InsertReplaceUserSongUseCase
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddSongViewModelDeps @Inject constructor(): ViewModelDeps() {
    @Inject
    lateinit var insertReplaceUserSongUseCase: InsertReplaceUserSongUseCase

    @Inject
    lateinit var addSongToCloudUseCase: AddSongToCloudUseCase
}