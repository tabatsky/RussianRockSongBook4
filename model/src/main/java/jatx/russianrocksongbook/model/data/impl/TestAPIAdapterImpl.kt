package jatx.russianrocksongbook.model.data.impl

import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import it.czerwinski.android.hilt.annotations.TestBoundTo
import jatx.russianrocksongbook.model.api.gson.*
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.data.SongBookAPIAdapter
import jatx.russianrocksongbook.model.data.SongRepository
import jatx.russianrocksongbook.model.domain.AppCrash
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.model.preferences.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

const val PAGE_SIZE = 15

@Singleton
@TestBoundTo(supertype = SongBookAPIAdapter::class, component = SingletonComponent::class)
class TestAPIAdapterImpl @Inject constructor(
    songRepo: SongRepository,
    userInfo: UserInfo
): SongBookAPIAdapter {
    private val list1 = songRepo.getSongsByArtistAsList("Немного Нервно")
    private val list2 = songRepo.getSongsByArtistAsList("Александр Башлачёв")
    private val list3 = songRepo.getSongsByArtistAsList("Сплин")
    private val list = list1
        .plus(list2)
        .plus(list3)
        .map {
            CloudSong(it, userInfo)
        }

    var isOnline = true

    fun search(searchFor: String, orderBy: OrderBy) = list
        .filter {
            it.artist.lowercase().contains(searchFor.lowercase())
                .or(it.title.lowercase().contains(searchFor.lowercase()))
        }
        .sortedBy {
            when (orderBy) {
                OrderBy.BY_ARTIST -> it.artist + it.title
                OrderBy.BY_TITLE -> it.title + it.artist
                OrderBy.BY_ID_DESC -> String.format("%04d", list.indexOf(it))
            }
        }


    override fun sendCrash(appCrash: AppCrash): Single<ResultWithoutDataGson> {
        TODO("Not yet implemented")
    }

    override fun addSong(song: Song, userInfo: UserInfo): Single<ResultWithoutDataGson> {
        TODO("Not yet implemented")
    }

    override fun addSongList(
        songs: List<Song>,
        userInfo: UserInfo
    ): Single<ResultWithAddSongListResultDataGson> {
        TODO("Not yet implemented")
    }

    override fun addWarning(song: Song, comment: String): Single<ResultWithoutDataGson> {
        TODO("Not yet implemented")
    }

    override fun addWarning(cloudSong: CloudSong, comment: String): Single<ResultWithoutDataGson> {
        TODO("Not yet implemented")
    }

    override fun searchSongs(
        searchFor: String,
        orderBy: OrderBy
    ): Single<ResultWithCloudSongListDataGson> {
        TODO("Not yet implemented")
    }

    override fun vote(
        cloudSong: CloudSong,
        userInfo: UserInfo,
        voteValue: Int
    ): Single<ResultWithNumberGson> = Single.just(
        ResultWithNumberGson(
            status = STATUS_SUCCESS,
            message = null,
            data = 1.0f
        )
    )

    override fun delete(
        secret1: String,
        secret2: String,
        cloudSong: CloudSong
    ): Single<ResultWithNumberGson> {
        TODO("Not yet implemented")
    }

    override fun getUploadsCountForUser(userInfo: UserInfo): Single<ResultWithNumberGson> {
        TODO("Not yet implemented")
    }

    override fun pagedSearch(
        searchFor: String,
        orderBy: OrderBy,
        page: Int
    ): ResultWithCloudSongListDataGson {
        Thread.sleep(100)
        return if (isOnline) {
            val list = search(searchFor, orderBy)
                .safeSublist((page - 1) * PAGE_SIZE, page * PAGE_SIZE)
                .map {
                    it.toCloudSongGson()
                }
            ResultWithCloudSongListDataGson(
                status = STATUS_SUCCESS,
                message = null,
                data = list
            )
        } else {
            ResultWithCloudSongListDataGson(
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