package jatx.russianrocksongbook.domain.repository.cloud

import io.reactivex.Single
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

    fun sendCrash(appCrash: AppCrash): Single<ResultWithoutData>
    fun addCloudSong(cloudSong: CloudSong): Single<ResultWithoutData>
    fun addCloudSongList(cloudSongs: List<CloudSong>):
            Single<ResultWithAddSongListResultData>
    fun addWarning(warning: Warning): Single<ResultWithoutData>
    fun searchSongs(searchFor: String, orderBy: OrderBy = OrderBy.BY_ID_DESC):
            Single<ResultWithCloudSongListData>
    fun vote(cloudSong: CloudSong, userInfo: UserInfo, voteValue: Int):
            Single<ResultWithNumber>
    fun delete(secret1: String, secret2: String, cloudSong: CloudSong):
            Single<ResultWithNumber>
    fun getUploadsCountForUser(userInfo: UserInfo):
            Single<ResultWithNumber>

    fun pagedSearch(searchFor: String, orderBy: OrderBy = OrderBy.BY_ID_DESC, page: Int):
            ResultWithCloudSongListData

    fun search(searchFor: String, orderBy: OrderBy): List<CloudSong>
}

enum class OrderBy(
    val orderBy: String,
    val orderByRus: String
) {
    BY_ID_DESC("byIdDesc", "Последние добавленные"),
    BY_ARTIST("byArtist", "По исполнителю"),
    BY_TITLE("byTitle", "По названию")
}