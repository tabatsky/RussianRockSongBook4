package jatx.russianrocksongbook.cloudsongs.internal.paging

import androidx.paging.compose.LazyPagingItems
import jatx.russianrocksongbook.domain.models.CloudSong

internal class ItemsAdapter(
    private val lazyItems: LazyPagingItems<CloudSong>?
) {
    val size: Int
        get() = lazyItems?.itemCount ?: 0

    fun getItem(position: Int): CloudSong? {
            val item = when {
                size == 0 -> {
                    null
                }
                position < size -> {
                    lazyItems?.get(position)
                }
                else -> {
                    lazyItems?.get(size - 1)
                    null
                }
            }
            return item
    }
}
