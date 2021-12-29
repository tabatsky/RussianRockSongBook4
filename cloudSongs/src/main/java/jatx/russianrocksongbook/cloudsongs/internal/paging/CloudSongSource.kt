package jatx.russianrocksongbook.cloudsongs.internal.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import jatx.russianrocksongbook.domain.repository.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.OrderBy
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.usecase.PagedSearchUseCase
import jatx.russianrocksongbook.util.debug.exceptionToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CloudSongSource(
    private val pagedSearchUseCase: PagedSearchUseCase,
    private val searchFor: String = "",
    private val orderBy: OrderBy = OrderBy.BY_ID_DESC,
    private val onFetchDataError: () -> Unit = {}
): PagingSource<Int, CloudSong>() {

    override val jumpingSupported = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CloudSong> {
        return withContext(Dispatchers.IO) {
            try {
                val nextPage = params.key ?: 1

                val result = pagedSearchUseCase
                        .execute(searchFor, orderBy, nextPage)

                if (result.status == STATUS_SUCCESS) {
                    val data = (result.data ?: listOf())

                    LoadResult.Page(
                        data = data,
                        prevKey = if (nextPage == 1) null else nextPage - 1,
                        nextKey = nextPage + 1
                    )
                } else {
                    throw ServerException()
                }
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

class ServerException: Exception()