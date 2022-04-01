package jatx.russianrocksongbook.networking.repository

import com.google.gson.Gson
import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithAddSongListResultData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithoutData
import jatx.russianrocksongbook.networking.converters.toAppCrashJson
import jatx.russianrocksongbook.networking.gson.WarningGson
import jatx.russianrocksongbook.networking.gson.toResultWithCloudSongListData
import jatx.russianrocksongbook.networking.songbookapi.RetrofitClient
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.toCloudSong
import jatx.russianrocksongbook.networking.converters.toCloudSongGson
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