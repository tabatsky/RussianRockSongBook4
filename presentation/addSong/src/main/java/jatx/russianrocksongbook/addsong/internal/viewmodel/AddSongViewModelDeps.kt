package jatx.russianrocksongbook.addsong.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.usecase.cloud.AddSongToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.local.InsertReplaceUserSongUseCase
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class AddSongViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val insertReplaceUserSongUseCase: InsertReplaceUserSongUseCase,
    val addSongToCloudUseCase: AddSongToCloudUseCase
)