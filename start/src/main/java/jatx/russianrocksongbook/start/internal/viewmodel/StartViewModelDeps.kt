package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.repository.SongRepository
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@ActivityRetainedScoped
open class StartViewModelDeps @Inject constructor(): ViewModelDeps() {
    @Inject
    lateinit var songRepository: SongRepository
}