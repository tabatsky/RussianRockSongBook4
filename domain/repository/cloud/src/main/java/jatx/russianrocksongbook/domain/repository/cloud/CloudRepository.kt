package jatx.russianrocksongbook.domain.repository.cloud

import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithAddSongListResultData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithNumber
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithoutData
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.warning.Warning

interface CloudRepository {
    var isOnline: Boolean

    suspend fun sendCrash(appCrash: AppCrash): ResultWithoutData
    suspend fun addCloudSong(cloudSong: CloudSong): ResultWithoutData
    suspend fun addCloudSongList(cloudSongs: List<CloudSong>):
            ResultWithAddSongListResultData
    suspend fun addWarning(warning: Warning): ResultWithoutData
    suspend fun searchSongs(searchFor: String, orderBy: CloudSearchOrderBy = CloudSearchOrderBy.BY_ID_DESC):
            ResultWithCloudSongListData
    suspend fun vote(cloudSong: CloudSong, userInfo: UserInfo, voteValue: Int):
            ResultWithNumber
    suspend fun delete(secret1: String, secret2: String, cloudSong: CloudSong):
            ResultWithNumber

    suspend fun pagedSearch(searchFor: String, orderBy: CloudSearchOrderBy = CloudSearchOrderBy.BY_ID_DESC, page: Int):
            ResultWithCloudSongListData

    fun search(searchFor: String, orderBy: CloudSearchOrderBy): List<CloudSong>
}

enum class CloudSearchOrderBy(
    val orderBy: String,
    val orderByRus: String
) {
    BY_ID_DESC("byIdDesc", "Последние добавленные"),
    BY_ARTIST("byArtist", "По исполнителю"),
    BY_TITLE("byTitle", "По названию")
}