package jatx.russianrocksongbook.addsong.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddSongStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    val addSongStateFlow = MutableStateFlow(AddSongState.initial())
}