package jatx.russianrocksongbook.networking.repository

import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.TestBoundTo
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.asCloudSongWithUserInfo
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.russianrocksongbook.domain.repository.cloud.result.*
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

const val PAGE_SIZE = 15

@Singleton
@TestBoundTo(supertype = CloudRepository::class, component = SingletonComponent::class)
class CloudRepositoryTestImpl @Inject constructor(
    private val localRepo: LocalRepository,
    private val userInfo: UserInfo
): CloudRepository {
    override var isOnline = true

    private val list1 by lazy {
        runBlocking {
            localRepo.getSongsByArtist("Немного Нервно")
        }
    }
    private val list2 by lazy {
        runBlocking {
            localRepo.getSongsByArtist("Александр Башлачёв")
        }
    }
    private val list3 by lazy {
        runBlocking {
            localRepo.getSongsByArtist("Сплин")
        }
    }

    private val arrayList by lazy {
        arrayListOf<Song>().apply {
            addAll(list1)
            addAll(list2)
            addAll(list3)
            reverse()
        }
    }

    private val _list: AtomicReference<List<CloudSong>?> = AtomicReference(null)
    private var list: List<CloudSong>
        get() {
            if (_list.get() == null) {
                val tmpList = arrayList
                    .map {
                        (it asCloudSongWithUserInfo userInfo).copy(variant = 1)
                    }
                _list.compareAndSet(null, tmpList)
            }
            return _list.get()!!
        }
        set(value) {
            _list.set(value)
        }

    private val orderByLambda: (CloudSong, CloudSearchOrderBy) -> String = { cloudSong, orderBy ->
        when (orderBy) {
            CloudSearchOrderBy.BY_ARTIST -> cloudSong.artist + cloudSong.title
            CloudSearchOrderBy.BY_TITLE -> cloudSong.title + cloudSong.artist
            CloudSearchOrderBy.BY_ID_DESC -> String.format("%04d", list.size - list.indexOf(cloudSong))
        }
    }

    override fun search(searchFor: String, orderBy: CloudSearchOrderBy) = list
        .filter {
            it.artist.lowercase().contains(searchFor.lowercase())
                .or(it.title.lowercase().contains(searchFor.lowercase()))
        }
        .sortedBy {
            orderByLambda(it, orderBy)
        }


    override suspend fun sendCrash(appCrash: AppCrash): ResultWithoutData {
        TODO("Not yet implemented")
    }

    override suspend fun addCloudSong(cloudSong: CloudSong): ResultWithoutData {
        val oldList = list
        val newList = oldList + listOf(cloudSong)
        list = newList
        return ResultWithoutData(STATUS_SUCCESS, null)
    }

    override suspend fun addCloudSongList(cloudSongs: List<CloudSong>): ResultWithAddSongListResultData {
        TODO("Not yet implemented")
    }

    override suspend fun addWarning(warning: Warning): ResultWithoutData =
        ResultWithoutData(STATUS_SUCCESS, null)

    override suspend fun searchSongs(
        searchFor: String,
        orderBy: CloudSearchOrderBy
    ): ResultWithCloudSongListData {
        TODO("Not yet implemented")
    }

    override suspend fun vote(
        cloudSong: CloudSong,
        userInfo: UserInfo,
        voteValue: Int
    ): ResultWithNumber = ResultWithNumber(
        status = STATUS_SUCCESS,
        message = null,
        data = 1.0f
    )


    override suspend fun delete(
        secret1: String,
        secret2: String,
        cloudSong: CloudSong
    ): ResultWithNumber {
        TODO("Not yet implemented")
    }

    override suspend fun pagedSearch(
        searchFor: String,
        orderBy: CloudSearchOrderBy,
        page: Int
    ): ResultWithCloudSongListData {
        delay(100)
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