package jatx.russianrocksongbook.model.data

import io.reactivex.Single
import jatx.russianrocksongbook.model.api.gson.ResultWithAddSongListResultDataGson
import jatx.russianrocksongbook.model.api.gson.ResultWithCloudSongListDataGson
import jatx.russianrocksongbook.model.api.gson.ResultWithNumberGson
import jatx.russianrocksongbook.model.api.gson.ResultWithoutDataGson
import jatx.russianrocksongbook.model.domain.AppCrash
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.model.preferences.UserInfo

interface SongBookAPIAdapter {
    fun sendCrash(appCrash: AppCrash): Single<ResultWithoutDataGson>
    fun addSong(song: Song, userInfo: UserInfo): Single<ResultWithoutDataGson>
    fun addSongList(songs: List<Song>, userInfo: UserInfo):
            Single<ResultWithAddSongListResultDataGson>
    fun addWarning(song: Song, comment: String): Single<ResultWithoutDataGson>
    fun addWarning(cloudSong: CloudSong, comment: String): Single<ResultWithoutDataGson>
    fun searchSongs(searchFor: String, orderBy: OrderBy = OrderBy.BY_ID_DESC):
            Single<ResultWithCloudSongListDataGson>
    fun vote(cloudSong: CloudSong, userInfo: UserInfo, voteValue: Int):
            Single<ResultWithNumberGson>
    fun delete(secret1: String, secret2: String, cloudSong: CloudSong):
            Single<ResultWithNumberGson>
    fun getUploadsCountForUser(userInfo: UserInfo):
            Single<ResultWithNumberGson>

    fun pagedSearch(searchFor: String, orderBy: OrderBy = OrderBy.BY_ID_DESC, page: Int):
            ResultWithCloudSongListDataGson
}

enum class OrderBy(
    val orderBy: String,
    val orderByRus: String
) {
    BY_ID_DESC("byIdDesc", "Последние добавленные"),
    BY_ARTIST("byArtist", "По исполнителю"),
    BY_TITLE("byTitle", "По названию")
}