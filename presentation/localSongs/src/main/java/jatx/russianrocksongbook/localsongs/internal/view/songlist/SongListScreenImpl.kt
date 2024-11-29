package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel

@Composable
internal fun SongListScreenImpl(
    artist: String,
    isBackFromSomeScreen: Boolean,
    songTitleToPass: String?
) {
    val localViewModel = LocalViewModel.getInstance()
    InitVoiceCommandViewModel()

    val localState by localViewModel.localStateFlow.collectAsState()
    val commonSongTextState by localViewModel.commonSongTextStateFlow.collectAsState()
    val appState by localViewModel.appStateFlow.collectAsState()

    val artistList = appState.artistList
    val currentArtist = appState.currentArtist

    val songList = localState.currentSongList

    val songListScrollPosition = commonSongTextState.songListScrollPosition
    val songListNeedScroll = commonSongTextState.songListNeedScroll

    val menuExpandedArtistGroup = localState.menuExpandedArtistGroup
    val menuScrollPosition = localState.menuScrollPosition

    val voiceHelpDontAsk by localViewModel.settings.voiceHelpDontAskState.collectAsState()

    val submitAction = localViewModel::submitAction

    SongListScreenImplContent(
        artist = artist,
        isBackFromSomeScreen = isBackFromSomeScreen,
        songTitleToPass = songTitleToPass,
        artistList = artistList,
        currentArtist = currentArtist,
        songList = songList,
        songListScrollPosition = songListScrollPosition,
        songListNeedScroll = songListNeedScroll,
        menuExpandedArtistGroup = menuExpandedArtistGroup,
        menuScrollPosition = menuScrollPosition,
        voiceHelpDontAsk = voiceHelpDontAsk,
        submitAction = submitAction
    )
}

@Composable
private fun InitVoiceCommandViewModel() {
    VoiceCommandViewModel.getInstance()
}