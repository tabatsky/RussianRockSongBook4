package jatx.russianrocksongbook.addartist.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddArtistStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    val addArtistStateFlow = MutableStateFlow(
        AddArtistState.initial()
    )
}