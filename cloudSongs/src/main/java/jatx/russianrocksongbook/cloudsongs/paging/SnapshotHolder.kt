package jatx.russianrocksongbook.cloudsongs.paging

import androidx.paging.ItemSnapshotList
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.CloudSong
import javax.inject.Inject

@ActivityRetainedScoped
class SnapshotHolder @Inject constructor(){
    var snapshot: ItemSnapshotList<CloudSong>? = null
    var isFlowInitDone = false
}