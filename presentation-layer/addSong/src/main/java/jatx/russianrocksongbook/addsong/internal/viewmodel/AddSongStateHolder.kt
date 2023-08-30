package jatx.russianrocksongbook.addsong.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddSongStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val addSongState = MutableStateFlow(AddSongUIState.initial())
}