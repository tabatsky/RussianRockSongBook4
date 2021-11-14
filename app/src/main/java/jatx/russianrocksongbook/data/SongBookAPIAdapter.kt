package jatx.russianrocksongbook.data

import com.google.gson.Gson
import io.reactivex.Single
import jatx.russianrocksongbook.api.RetrofitClient
import jatx.russianrocksongbook.data.gson.ResultWithAddSongListResultDataGson
import jatx.russianrocksongbook.data.gson.ResultWithoutDataGson
import jatx.russianrocksongbook.data.gson.WarningGson
import jatx.russianrocksongbook.domain.AppCrash
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.preferences.UserInfo
import javax.inject.Inject

class SongBookAPIAdapter @Inject constructor(
    private val retrofitClient: RetrofitClient
) {
    fun sendCrash(appCrash: AppCrash): Single<ResultWithoutDataGson> {
        val params = mapOf(
            "appCrashJSON" to Gson().toJson(appCrash.toAppCrashJson())
        )
        return retrofitClient.songBookAPI.sendCrash(params)
    }

    fun addSong(song: Song, userInfo: UserInfo): Single<ResultWithoutDataGson> {
        val cloudSong = CloudSong(song, userInfo)
        val params = mapOf(
            "cloudSongJSON" to Gson().toJson(cloudSong.toCloudSongGson())
        )
        return retrofitClient.songBookAPI.addSong(params)
    }

    fun addSongList(songs: List<Song>, userInfo: UserInfo): Single<ResultWithAddSongListResultDataGson> {
        val cloudSongs = songs.map { CloudSong(it, userInfo).toCloudSongGson() }
        val params = mapOf(
            "cloudSongListJSON" to Gson().toJson(cloudSongs)
        )
        return retrofitClient.songBookAPI.addSongList(params)
    }

    fun addWarning(song: Song, comment: String): Single<ResultWithoutDataGson> {
        val warning = WarningGson(song, comment)
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning)
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    fun addWarning(cloudSong: CloudSong, comment: String): Single<ResultWithoutDataGson> {
        val warning = WarningGson(cloudSong, comment)
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning)
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    fun searchSongs(searchFor: String, orderBy: OrderBy = OrderBy.BY_ID_DESC)
            = retrofitClient.songBookAPI.searchSongs(searchFor, orderBy.orderBy)

    fun vote(cloudSong: CloudSong, userInfo: UserInfo, voteValue: Int) = retrofitClient.songBookAPI.vote(
        userInfo.googleAccount,
        userInfo.deviceIdHash,
        cloudSong.artist,
        cloudSong.title,
        cloudSong.variant,
        voteValue
    )

    fun delete(
        secret1: String,
        secret2: String,
        cloudSong: CloudSong
    ) = retrofitClient
        .songBookAPI
        .delete(
            secret1,
            secret2,
            cloudSong.artist,
            cloudSong.title,
            cloudSong.variant
        )

    fun getUploadsCountForUser(userInfo: UserInfo) = retrofitClient.songBookAPI.getUploadsCountForUser(
        userInfo.googleAccount,
        userInfo.deviceIdHash
    )
}


enum class OrderBy(
    val orderBy: String,
    val orderByRus: String
) {
    BY_ID_DESC("byIdDesc", "Последние добавленные"),
    BY_ARTIST("byArtist", "По исполнителю"),
    BY_TITLE("byTitle", "По названию")
}