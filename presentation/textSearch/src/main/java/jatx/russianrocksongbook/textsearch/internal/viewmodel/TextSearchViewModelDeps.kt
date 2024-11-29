package jatx.russianrocksongbook.textsearch.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextViewModelDeps
import jatx.russianrocksongbook.domain.usecase.local.GetSongsByTextSearchUseCase
import javax.inject.Inject

@ViewModelScoped
class TextSearchViewModelDeps @Inject constructor(
    val commonSongTextViewModelDeps: CommonSongTextViewModelDeps,
    val getSongsByTextSearchUseCase: GetSongsByTextSearchUseCase
)
