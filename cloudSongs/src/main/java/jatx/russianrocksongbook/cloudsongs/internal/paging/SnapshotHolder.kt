package jatx.russianrocksongbook.cloudsongs.internal.paging

import androidx.paging.ItemSnapshotList
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.models.CloudSong
import javax.inject.Inject

@ActivityRetainedScoped
internal class SnapshotHolder @Inject constructor(){
    var snapshot: ItemSnapshotList<CloudSong>? = null
    var isFlowInitDone = false
}