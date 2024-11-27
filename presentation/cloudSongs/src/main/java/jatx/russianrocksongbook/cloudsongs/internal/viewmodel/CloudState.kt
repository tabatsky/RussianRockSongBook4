package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import androidx.paging.PagingData
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import kotlinx.coroutines.flow.Flow

data class CloudState(
    val searchState: SearchState,
    val currentCloudSongCount: Int,
    val cloudSongPosition: Int,
    val currentCloudSong: CloudSong?,
    val cloudSongsFlow: Flow<PagingData<CloudSong>>?,
    val searchFor: String,
    val orderBy: CloudSearchOrderBy,
    val scrollPosition: Int,
    val needScroll: Boolean
) {
    companion object {
        fun initial() = CloudState(
            searchState = SearchState.LOADING_FIRST_PAGE,
            currentCloudSongCount = 0,
            cloudSongPosition = 0,
            currentCloudSong = null,
            cloudSongsFlow = null,
            searchFor = "",
            orderBy = CloudSearchOrderBy.BY_ID_DESC,
            scrollPosition = 0,
            needScroll = false
        )
    }
}
