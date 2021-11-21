package jatx.russianrocksongbook.data

import io.reactivex.Single
import jatx.russianrocksongbook.api.gson.*
import jatx.russianrocksongbook.domain.AppCrash
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.preferences.UserInfo

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
}

enum class OrderBy(
    val orderBy: String,
    val orderByRus: String
) {
    BY_ID_DESC("byIdDesc", "Последние добавленные"),
    BY_ARTIST("byArtist", "По исполнителю"),
    BY_TITLE("byTitle", "По названию")
}