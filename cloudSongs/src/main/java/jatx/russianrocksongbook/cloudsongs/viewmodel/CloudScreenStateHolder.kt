package jatx.russianrocksongbook.cloudsongs.viewmodel

import androidx.paging.Pager
import androidx.paging.PagingData
import jatx.russianrocksongbook.cloudsongs.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.paging.CloudSongSource
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.data.SongBookAPIAdapter
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudScreenStateHolder @Inject constructor(
    val screenStateHolder: ScreenStateHolder,
    private val songBookAPIAdapter: SongBookAPIAdapter
) {

    val isCloudLoading = MutableStateFlow(false)
    val cloudSongCount = MutableStateFlow(0)
    val cloudSongPosition = MutableStateFlow(0)
    var listPosition = MutableStateFlow(0)
    var latestPosition = MutableStateFlow(-1)
    var wasFetchDataError = MutableStateFlow(false)
    val cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
    val cloudSongsFlow: MutableStateFlow<Flow<PagingData<CloudSong>>> = MutableStateFlow(
        Pager(CONFIG) {
            CloudSongSource(songBookAPIAdapter, "", OrderBy.BY_ID_DESC)
        }.flow
    )
}