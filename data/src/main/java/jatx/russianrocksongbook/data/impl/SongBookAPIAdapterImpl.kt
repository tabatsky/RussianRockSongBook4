package jatx.russianrocksongbook.data.impl

import com.google.gson.Gson
import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.api.RetrofitClient
import jatx.russianrocksongbook.api.gson.ResultWithAddSongListResultDataGson
import jatx.russianrocksongbook.api.gson.ResultWithoutDataGson
import jatx.russianrocksongbook.api.gson.WarningGson
import jatx.russianrocksongbook.data.OrderBy
import jatx.russianrocksongbook.data.SongBookAPIAdapter
import jatx.russianrocksongbook.domain.AppCrash
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.preferences.UserInfo
import javax.inject.Inject

@BoundTo(supertype = SongBookAPIAdapter::class, component = SingletonComponent::class)
class SongBookAPIAdapterImpl @Inject constructor(
    private val retrofitClient: RetrofitClient
): SongBookAPIAdapter {
    override fun sendCrash(appCrash: AppCrash): Single<ResultWithoutDataGson> {
        val params = mapOf(
            "appCrashJSON" to Gson().toJson(appCrash.toAppCrashJson())
        )
        return retrofitClient.songBookAPI.sendCrash(params)
    }

    override fun addSong(song: Song, userInfo: UserInfo): Single<ResultWithoutDataGson> {
        val cloudSong = CloudSong(song, userInfo)
        val params = mapOf(
            "cloudSongJSON" to Gson().toJson(cloudSong.toCloudSongGson())
        )
        return retrofitClient.songBookAPI.addSong(params)
    }

    override fun addSongList(songs: List<Song>, userInfo: UserInfo): Single<ResultWithAddSongListResultDataGson> {
        val cloudSongs = songs.map { CloudSong(it, userInfo).toCloudSongGson() }
        val params = mapOf(
            "cloudSongListJSON" to Gson().toJson(cloudSongs)
        )
        return retrofitClient.songBookAPI.addSongList(params)
    }

    override fun addWarning(song: Song, comment: String): Single<ResultWithoutDataGson> {
        val warning = WarningGson(song, comment)
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning)
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    override fun addWarning(cloudSong: CloudSong, comment: String): Single<ResultWithoutDataGson> {
        val warning = WarningGson(cloudSong, comment)
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning)
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    override fun searchSongs(searchFor: String, orderBy: OrderBy)
            = retrofitClient.songBookAPI.searchSongs(searchFor, orderBy.orderBy)

    override fun vote(cloudSong: CloudSong, userInfo: UserInfo, voteValue: Int) = retrofitClient.songBookAPI.vote(
        userInfo.googleAccount,
        userInfo.deviceIdHash,
        cloudSong.artist,
        cloudSong.title,
        cloudSong.variant,
        voteValue
    )

    override fun delete(
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

    override fun getUploadsCountForUser(userInfo: UserInfo) = retrofitClient.songBookAPI.getUploadsCountForUser(
        userInfo.googleAccount,
        userInfo.deviceIdHash
    )
}
