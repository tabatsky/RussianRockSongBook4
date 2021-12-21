package jatx.russianrocksongbook.cloudsongs.paging

import androidx.paging.compose.LazyPagingItems
import jatx.russianrocksongbook.domain.CloudSong

class ItemsAdapter(
    private val snapshotHolder: SnapshotHolder,
    private val lazyItems: LazyPagingItems<CloudSong>
) {
    private val snapshotSize: Int
        get() = snapshotHolder.snapshot?.size ?: 0

    val size: Int
        get() = max(snapshotSize, lazyItems.itemCount)

    fun getItem(position: Int): CloudSong? {
        if (position < snapshotSize) {
            if (position < lazyItems.itemCount) {
                lazyItems[position]
            } else if (lazyItems.itemCount > 0)  {
                lazyItems[lazyItems.itemCount - 1]
            }
            return snapshotHolder.snapshot?.get(position)
        } else {
            val item = if (position < lazyItems.itemCount) {
                lazyItems[position]
            } else {
                lazyItems[lazyItems.itemCount - 1]
                null
            }
            snapshotHolder.snapshot = lazyItems.itemSnapshotList
            return item
        }
    }
}

private fun max(a: Int, b: Int) = if (a > b) a else b