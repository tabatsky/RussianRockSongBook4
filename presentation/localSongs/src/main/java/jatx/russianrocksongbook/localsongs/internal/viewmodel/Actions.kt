package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song

data class UpdateMenuScrollPosition(val position: Int): UIAction
data class UpdateMenuExpandedArtistGroup(val artistGroup: String): UIAction
data class UpdateSongListScrollPosition(val position: Int): UIAction
data class UpdateSongListNeedScroll(val need: Boolean): UIAction
object UpdateArtists: UIAction
data class SpeechRecognize(val dontAskMore: Boolean): UIAction
object ReviewApp: UIAction
object ShowDevSite: UIAction
data class SelectArtist(val artist: String): UIAction

data class ParseAndExecuteVoiceCommand(val command: String): UIAction
