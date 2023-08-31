package jatx.russianrocksongbook.addartist.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class AddArtistStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val addArtistState = MutableStateFlow(
        AddArtistState.initial()
    )
}