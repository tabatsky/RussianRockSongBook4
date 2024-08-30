package jatx.russianrocksongbook.textsearch.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import javax.inject.Inject

@ViewModelScoped
class CloudViewModelDeps @Inject constructor(
    val commonViewModelDeps: CommonViewModelDeps
)
