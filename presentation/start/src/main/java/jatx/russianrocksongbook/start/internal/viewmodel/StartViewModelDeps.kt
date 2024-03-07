package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.repository.local.init.LocalRepositoryInitializer
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class StartViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val localRepoInitializer: LocalRepositoryInitializer
)