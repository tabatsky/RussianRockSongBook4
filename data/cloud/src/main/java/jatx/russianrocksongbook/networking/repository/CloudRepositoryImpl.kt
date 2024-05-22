package jatx.russianrocksongbook.networking.repository

import com.google.gson.Gson
import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithAddSongListResultData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithoutData
import jatx.russianrocksongbook.networking.converters.toAppCrashApiModel
import jatx.russianrocksongbook.networking.converters.toCloudSongApiModel
import jatx.russianrocksongbook.networking.converters.toWarningApiModel
import jatx.russianrocksongbook.networking.apimodels.toResultWithCloudSongListData
import jatx.russianrocksongbook.networking.songbookapi.RetrofitClient
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = CloudRepository::class, component = SingletonComponent::class)
class CloudRepositoryImpl @Inject constructor(
    private val retrofitClient: RetrofitClient
): CloudRepository {
    override var isOnline = true

    override suspend fun sendCrash(appCrash: AppCrash): ResultWithoutData {
        val params = mapOf(
            "appCrashJSON" to Gson().toJson(appCrash.toAppCrashApiModel())
        )
        return retrofitClient.songBookAPI.sendCrash(params)
    }

    override suspend fun addCloudSong(cloudSong: CloudSong): ResultWithoutData {
        val params = mapOf(
            "cloudSongJSON" to Gson().toJson(cloudSong.toCloudSongApiModel())
        )
        return retrofitClient.songBookAPI.addSong(params)
    }

    override suspend fun addCloudSongList(cloudSongs: List<CloudSong>): ResultWithAddSongListResultData {
        val cloudSongsGson = cloudSongs.map { it.toCloudSongApiModel() }
        val params = mapOf(
            "cloudSongListJSON" to Gson().toJson(cloudSongsGson)
        )
        return retrofitClient.songBookAPI.addSongList(params)
    }

    override suspend fun addWarning(warning: Warning): ResultWithoutData {
        val params = mapOf(
            "warningJSON" to Gson().toJson(warning.toWarningApiModel())
        )
        return retrofitClient.songBookAPI.addWarning(params)
    }

    override suspend fun searchSongs(searchFor: String, orderBy: OrderBy) = retrofitClient
        .songBookAPI
        .searchSongs(searchFor, orderBy.orderBy)
        .toResultWithCloudSongListData()

    override suspend fun vote(cloudSong: CloudSong, userInfo: UserInfo, voteValue: Int) = retrofitClient.songBookAPI.vote(
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

    override suspend fun pagedSearch(
        searchFor: String,
        orderBy: OrderBy,
        page: Int
    ): ResultWithCloudSongListData = retrofitClient
        .songBookAPI
        .pagedSearch(searchFor, orderBy.orderBy, page)
        .toResultWithCloudSongListData()

    override fun search(searchFor: String, orderBy: OrderBy) =
        runBlocking {
            searchSongs(searchFor, orderBy)
                .data
        } ?: listOf()
}