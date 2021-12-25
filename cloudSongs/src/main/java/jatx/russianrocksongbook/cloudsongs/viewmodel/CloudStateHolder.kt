package jatx.russianrocksongbook.cloudsongs.viewmodel

import androidx.paging.Pager
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.cloudsongs.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.paging.CloudSongSource
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.networking.api.OrderBy
import jatx.russianrocksongbook.networking.api.SongBookAPIAdapter
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class CloudStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder,
    private val songBookAPIAdapter: SongBookAPIAdapter
) {

    val isCloudLoading = MutableStateFlow(false)
    val isListEmpty = MutableStateFlow(false)
    val cloudSongCount = MutableStateFlow(0)
    val cloudSongPosition = MutableStateFlow(0)
    var wasFetchDataError = MutableStateFlow(false)
    val cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
    val cloudSongsFlow: MutableStateFlow<Flow<PagingData<CloudSong>>> = MutableStateFlow(
        Pager(CONFIG) {
            CloudSongSource(songBookAPIAdapter, "", OrderBy.BY_ID_DESC)
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