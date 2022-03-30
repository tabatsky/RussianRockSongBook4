package jatx.russianrocksongbook.networking.repository

import com.google.gson.Gson
import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.AppCrash
import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.OrderBy
import jatx.russianrocksongbook.domain.repository.CloudRepository
import jatx.russianrocksongbook.networking.ext.toCloudSong
import jatx.russianrocksongbook.networking.ext.toCloudSongGson
import jatx.russianrocksongbook.domain.repository.result.ResultWithAddSongListResultData
import jatx.russianrocksongbook.domain.repository.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.domain.repository.result.ResultWithoutData
import jatx.russianrocksongbook.networking.ext.toAppCrashJson
import jatx.russianrocksongbook.networking.gson.WarningGson
import jatx.russianrocksongbook.networking.gson.toResultWithCloudSongListData
import jatx.russianrocksongbook.networking.songbookapi.RetrofitClient
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = CloudRepository::class, component = SingletonComponent::class)
class CloudRepositoryImpl @Inject constructor(
    private val retrofitClient: RetrofitClient
): CloudRepository {
    override var isOnline = true

    override fun sendCrash(appCrash: AppCrash): Single<ResultWithoutData> {
        val params = mapOf(
            "appCrashJSON" to Gson().toJson(appCrash.toAppCrashJson())
        )
        return retrofitClient.songBookAPI.sendCrash(params)
    }

    override fun addSong(song: Song, userInfo: UserInfo): Single<ResultWithoutData> {
        val cloudSong = song.toCloudSong(userInfo)
        val params = mapOf(
            "cloudSongJSON" to Gson().toJson(cloudSong.toCloudSongGson())
        )
        return retrofitClient.songBookAPI.addSong(params)
    }

    override fun addSongList(songs: List<Song>, userInfo: UserInfo): Single<ResultWithAddSongListResultData> {
        val cloudSongs = songs.map { it.toCloudSong(userInfo).toCloudSongGson() }
        val params = mapOf(
            "cloudSongListJSON" to Gson().toJson(cloudSongs)
        )
        return retrofitClient.songBookAPI.addSongList(params)
    }

    override fun addWarning(song: Song, comment: String): Single<ResultWithoutData> {
        val warning = WarningGson(song, comment)
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning)
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    override fun addWarning(cloudSong: CloudSong, comment: String): Single<ResultWithoutData> {
        val warning = WarningGson(cloudSong, comment)
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning)
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    override fun searchSongs(searchFor: String, orderBy: OrderBy) = retrofitClient
        .songBookAPI
        .searchSongs(searchFor, orderBy.orderBy)
        .map { it.toResultWithCloudSongListData() }

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

    override fun pagedSearch(
        searchFor: String,
        orderBy: OrderBy,
        page: Int
    ): ResultWithCloudSongListData = retrofitClient
        .songBookAPI
        .pagedSearch(searchFor, orderBy.orderBy, page)
        .toResultWithCloudSongListData()

    override fun search(searchFor: String, orderBy: OrderBy) =
        searchSongs(searchFor, orderBy)
            .blockingGet()
            .data ?: listOf()
}