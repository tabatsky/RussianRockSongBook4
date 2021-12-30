package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import androidx.paging.Pager
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.cloudsongs.internal.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.internal.paging.CloudSongSource
import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.OrderBy
import jatx.russianrocksongbook.domain.usecase.PagedSearchUseCase
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class CloudStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder,
    private val pagedSearchUseCase: PagedSearchUseCase
) {

    val searchState = MutableStateFlow(SearchState.LOADING)
    val cloudSongCount = MutableStateFlow(0)
    val cloudSongPosition = MutableStateFlow(0)
    val cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
    val cloudSongsFlow: MutableStateFlow<Flow<PagingData<CloudSong>>> = MutableStateFlow(
        Pager(CONFIG) {
            CloudSongSource(pagedSearchUseCase, "", OrderBy.BY_ID_DESC)
        }.flow
    )
    val searchFor = MutableStateFlow("")
    val orderBy = MutableStateFlow(OrderBy.BY_ID_DESC)
    val invalidateCounter = MutableStateFlow(0)

    val scrollPosition = MutableStateFlow(0)
    val isLastOrientationPortrait = MutableStateFlow(true)
    val wasOrientationChanged = MutableStateFlow(false)
    val needScroll = MutableStateFlow(false)
}