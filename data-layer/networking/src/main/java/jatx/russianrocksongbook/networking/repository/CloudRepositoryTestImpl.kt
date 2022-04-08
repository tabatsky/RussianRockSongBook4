package jatx.russianrocksongbook.networking.repository

import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import it.czerwinski.android.hilt.annotations.TestBoundTo
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.toCloudSong
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.cloud.result.*
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

const val PAGE_SIZE = 15

@Singleton
@TestBoundTo(supertype = CloudRepository::class, component = SingletonComponent::class)
internal class CloudRepositoryTestImpl @Inject constructor(
    localRepo: LocalRepository,
    private val userInfo: UserInfo
): CloudRepository {
    override var isOnline = true

    private val list1 = localRepo.getSongsByArtistAsList("Немного Нервно")
    private val list2 = localRepo.getSongsByArtistAsList("Александр Башлачёв")
    private val list3 = localRepo.getSongsByArtistAsList("Сплин")

    private val arrayList = arrayListOf<Song>().apply {
        addAll(list1)
        addAll(list2)
        addAll(list3)
    }

    private var list: List<CloudSong> = arrayList
            .map {
                it.toCloudSong(userInfo).copy(variant = 1)
            }

    private val orderByLambda: (CloudSong, OrderBy) -> String = { cloudSong, orderBy ->
        when (orderBy) {
            OrderBy.BY_ARTIST -> cloudSong.artist + cloudSong.title
            OrderBy.BY_TITLE -> cloudSong.title + cloudSong.artist
            OrderBy.BY_ID_DESC -> String.format("%04d", list.size - list.indexOf(cloudSong))
        }
    }

    override fun search(searchFor: String, orderBy: OrderBy) = list
        .filter {
            it.artist.lowercase().contains(searchFor.lowercase())
                .or(it.title.lowercase().contains(searchFor.lowercase()))
        }
        .sortedBy {
            orderByLambda(it, orderBy)
        }


    override fun sendCrash(appCrash: AppCrash): Single<ResultWithoutData> {
        TODO("Not yet implemented")
    }

    override fun addCloudSong(cloudSong: CloudSong): Single<ResultWithoutData> {
        list = list + listOf(cloudSong)
        return Single.just(ResultWithoutData(STATUS_SUCCESS, null))
    }

    override fun addCloudSongList(cloudSongs: List<CloudSong>): Single<ResultWithAddSongListResultData> {
        TODO("Not yet implemented")
    }

    override fun addWarning(warning: Warning): Single<ResultWithoutData> {
        TODO("Not yet implemented")
    }

    override fun searchSongs(
        searchFor: String,
        orderBy: OrderBy
    ): Single<ResultWithCloudSongListData> {
        TODO("Not yet implemented")
    }

    override fun vote(
        cloudSong: CloudSong,
        userInfo: UserInfo,
        voteValue: Int
    ): Single<ResultWithNumber> = Single.just(
        ResultWithNumber(
            status = STATUS_SUCCESS,
            message = null,
            data = 1.0f
        )
    )

    override fun delete(
        secret1: String,
        secret2: String,
        cloudSong: CloudSong
    ): Single<ResultWithNumber> {
        TODO("Not yet implemented")
    }

    override fun getUploadsCountForUser(userInfo: UserInfo): Single<ResultWithNumber> {
        TODO("Not yet implemented")
    }

    override fun pagedSearch(
        searchFor: String,
        orderBy: OrderBy,
        page: Int
    ): ResultWithCloudSongListData {
        Thread.sleep(100)
        return if (isOnline) {
            val list = search(searchFor, orderBy)
                .safeSublist((page - 1) * PAGE_SIZE, page * PAGE_SIZE)
            ResultWithCloudSongListData(
                status = STATUS_SUCCESS,
                message = null,
                data = list
            )
        } else {
            ResultWithCloudSongListData(
                status = STATUS_ERROR,
                message = "Fake error",
                data = null
            )
        }
    }
}

fun <T: Any> List<T>.safeSublist(fromIndex: Int, toIndex: Int): List<T> {
    return when {
        fromIndex >= size -> listOf()
        toIndex >= size -> subList(fromIndex, size - 1)
        else -> subList(fromIndex, toIndex)
    }
}