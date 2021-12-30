package jatx.russianrocksongbook.domain.repository

import io.reactivex.Single
import jatx.russianrocksongbook.domain.models.AppCrash
import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.result.ResultWithAddSongListResultData
import jatx.russianrocksongbook.domain.repository.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.domain.repository.result.ResultWithNumber
import jatx.russianrocksongbook.domain.repository.result.ResultWithoutData
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo

interface CloudRepository {
    var isOnline: Boolean

    fun sendCrash(appCrash: AppCrash): Single<ResultWithoutData>
    fun addSong(song: Song, userInfo: UserInfo): Single<ResultWithoutData>
    fun addSongList(songs: List<Song>, userInfo: UserInfo):
            Single<ResultWithAddSongListResultData>
    fun addWarning(song: Song, comment: String): Single<ResultWithoutData>
    fun addWarning(cloudSong: CloudSong, comment: String): Single<ResultWithoutData>
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