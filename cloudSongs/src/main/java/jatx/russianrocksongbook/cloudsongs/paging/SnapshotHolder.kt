package jatx.russianrocksongbook.cloudsongs.paging

import androidx.paging.ItemSnapshotList
import jatx.russianrocksongbook.model.domain.CloudSong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnapshotHolder @Inject constructor(){
    var snapshot: ItemSnapshotList<CloudSong>? = null
    var flowOnEachCounter = 0
}