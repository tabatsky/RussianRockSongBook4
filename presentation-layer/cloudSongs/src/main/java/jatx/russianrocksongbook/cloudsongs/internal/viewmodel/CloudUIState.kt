package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import androidx.paging.PagingData
import jatx.russianrocksongbook.commonviewmodel.CommonUIState
import jatx.russianrocksongbook.commonviewmodel.UIState
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import kotlinx.coroutines.flow.Flow

data class CloudUIState(
    val commonUIState: CommonUIState,
    val searchState: SearchState,
    val cloudSongCount: Int,
    val cloudSongPosition: Int,
    val cloudSong: CloudSong?,
    val cloudSongsFlow: Flow<PagingData<CloudSong>>?,
    val searchFor: String,
    val orderBy: OrderBy,
    val invalidateCounter: Int,
    val scrollPosition: Int,
    val needScroll: Boolean
): UIState {
    companion object {
        fun initial(commonUIState: CommonUIState) = CloudUIState(
            commonUIState = commonUIState,
            searchState = SearchState.LOADING,
            cloudSongCount = 0,
            cloudSongPosition = 0,
            cloudSong = null,
            cloudSongsFlow = null,
            searchFor = "",
            orderBy = OrderBy.BY_ID_DESC,
            invalidateCounter = 0,
            scrollPosition = 0,
            needScroll = false
        )
    }
}
