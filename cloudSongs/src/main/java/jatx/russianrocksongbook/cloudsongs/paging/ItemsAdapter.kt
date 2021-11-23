package jatx.russianrocksongbook.cloudsongs.paging

import androidx.paging.compose.LazyPagingItems
import jatx.russianrocksongbook.cloudsongs.viewmodel.CloudViewModel
import jatx.russianrocksongbook.model.domain.CloudSong

class ItemsAdapter(
    private val cloudViewModel: CloudViewModel,
    private val lazyItems: LazyPagingItems<CloudSong>
) {
    val snapshotSize: Int
        get() = cloudViewModel.snapshot?.size ?: 0

    val size: Int
        get() = max(snapshotSize, lazyItems.itemCount)

    fun getItem(position: Int): CloudSong? {
        if (position < snapshotSize) {
            if (position < lazyItems.itemCount) {
                lazyItems[position]
            } else if (lazyItems.itemCount > 0)  {
                lazyItems[lazyItems.itemCount - 1]
            }
            return cloudViewModel.snapshot?.get(position)
        } else {
            val item = if (position < lazyItems.itemCount) {
                lazyItems[position]
            } else {
                lazyItems[lazyItems.itemCount - 1]
                null
            }
            cloudViewModel.snapshot = lazyItems.itemSnapshotList
            return item
        }
    }
}

private fun max(a: Int, b: Int) = if (a > b) a else b