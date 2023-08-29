package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import androidx.paging.PagingData
import jatx.russianrocksongbook.commonviewmodel.UIState
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import kotlinx.coroutines.flow.Flow

data class CloudUIState(
    val searchState: SearchState,
    val currentCloudSongCount: Int,
    val cloudSongPosition: Int,
    val currentCloudSong: CloudSong?,
    val cloudSongsFlow: Flow<PagingData<CloudSong>>?,
    val searchFor: String,
    val orderBy: OrderBy,
    val invalidateCounter: Int,
    val scrollPosition: Int,
    val needScroll: Boolean
): UIState {
    companion object {
        fun initial() = CloudUIState(
            searchState = SearchState.LOADING,
            currentCloudSongCount = 0,
            cloudSongPosition = 0,
            currentCloudSong = null,
            cloudSongsFlow = null,
            searchFor = "",
            orderBy = OrderBy.BY_ID_DESC,
            invalidateCounter = 0,
            scrollPosition = 0,
            needScroll = false
        )
    }
}
