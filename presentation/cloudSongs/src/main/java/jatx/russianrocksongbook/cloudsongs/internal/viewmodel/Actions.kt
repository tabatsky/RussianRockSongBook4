package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy

data class PerformCloudSearch(val searchFor: String, val orderBy: OrderBy): UIAction
data class UpdateSearchState(val searchState: SearchState): UIAction
data class UpdateSearchFor(val searchFor: String): UIAction
data class UpdateOrderBy(val orderBy: OrderBy): UIAction
data class UpdateCurrentCloudSongCount(val count: Int): UIAction
data class UpdateCurrentCloudSong(val cloudSong: CloudSong?): UIAction
data class SelectCloudSong(val position: Int): UIAction
data class UpdateCloudSongListScrollPosition(val position: Int): UIAction
data class UpdateCloudSongListNeedScroll(val needScroll: Boolean): UIAction
object NextCloudSong: UIAction
object PrevCloudSong: UIAction
data class DeleteCurrentFromCloud(val secret1: String, val secret2: String): UIAction
object DownloadCurrent: UIAction
data class VoteForCurrent(val voteValue: Int): UIAction