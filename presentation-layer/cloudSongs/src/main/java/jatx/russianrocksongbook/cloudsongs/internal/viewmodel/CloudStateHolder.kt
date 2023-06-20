package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import androidx.paging.PagingData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class CloudStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {

    val searchState = MutableStateFlow(SearchState.LOADING)
    val cloudSongCount = MutableStateFlow(0)
    val cloudSongPosition = MutableStateFlow(0)
    val cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
    val cloudSongsFlow: MutableStateFlow<Flow<PagingData<CloudSong>>?> = MutableStateFlow(null)
    val searchFor = MutableStateFlow("")
    val orderBy = MutableStateFlow(OrderBy.BY_ID_DESC)
    val invalidateCounter = MutableStateFlow(0)

    val scrollPosition = MutableStateFlow(0)
    val needScroll = MutableStateFlow(false)
}