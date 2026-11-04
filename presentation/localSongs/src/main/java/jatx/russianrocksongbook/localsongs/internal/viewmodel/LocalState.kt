package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonappstate.UIStateKey
import jatx.russianrocksongbook.commonappstate.UIState
import jatx.russianrocksongbook.domain.models.local.Song

data class LocalState(
    val currentSongList: List<Song>,
    val menuScrollPosition: Int,
    val menuExpandedArtistGroup: String
) : UIState {

    companion object {
        fun initial() = LocalState(
            currentSongList = listOf(),
            menuScrollPosition = 0,
            menuExpandedArtistGroup = ""
        )
    }
}

/** У LocalViewModel и VoiceCommandViewModel свои независимые LocalState, поэтому ключ зависит от владельца. */
internal fun newLocalStateKey(owner: String) =
    UIStateKey(LocalState::class.java, "${LocalState::class.java.name}#$owner") { LocalState.initial() }
