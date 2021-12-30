package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.repository.LocalRepository
import jatx.russianrocksongbook.viewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
internal class StartViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps,
    val localRepository: LocalRepository
)