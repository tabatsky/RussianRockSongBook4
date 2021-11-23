package jatx.russianrocksongbook.cloudsongs.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.data.SongBookAPIAdapter
import jatx.russianrocksongbook.model.debug.exceptionToString
import jatx.russianrocksongbook.model.domain.CloudSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class CloudSongSource(
    private val songBookAPIAdapter: SongBookAPIAdapter,
    private val searchFor: String = "",
    private val orderBy: OrderBy = OrderBy.BY_ID_DESC,
    private val onFetchDataError: () -> Unit = {}
): PagingSource<Int, CloudSong>() {

    init {
        // TODO:
        // try to prevent recreating

        Log.e("CloudSongSource", "init")
        try {
            throw RuntimeException()
        } catch (e: Exception) {
            Log.e("exception", exceptionToString(e))
        }
    }

    private val cache = hashMapOf<Int, List<CloudSong>>()

    override val jumpingSupported = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CloudSong> {
        return withContext(Dispatchers.IO) {
            try {
                val nextPage = params.key ?: 1

                val data = if (cache.containsKey(nextPage))
                    cache[nextPage] ?: listOf()
                else {
                    val result = songBookAPIAdapter
                        .pagedSearch(searchFor, orderBy, nextPage)
                    val list = (result.data ?: listOf()).map { CloudSong(it) }
                    cache[nextPage] = list
                    list
                }

                LoadResult.Page(
                    data = data,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = nextPage + 1
                )
            } catch (e: Exception) {
                Log.e("load", exceptionToString(e))
                withContext(Dispatchers.Main) {
                    onFetchDataError()
                }
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CloudSong>): Int? {
        return null
    }

}