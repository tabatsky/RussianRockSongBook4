package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song

data class UpdateCurrentSong(val song: Song?): UIAction
data class UpdateMenuScrollPosition(val position: Int): UIAction
data class UpdateMenuExpandedArtistGroup(val artistGroup: String): UIAction
data class UpdateSongListScrollPosition(val position: Int): UIAction
data class UpdateSongListNeedScroll(val need: Boolean): UIAction
data class SetEditorMode(val isEditor: Boolean): UIAction
data class SetAutoPlayMode(val isAutoPlay: Boolean): UIAction
object UpdateArtists: UIAction
data class SelectSong(val position: Int): UIAction
object NextSong: UIAction
object PrevSong: UIAction
data class SaveSong(val song: Song): UIAction
data class SetFavorite(val favorite: Boolean): UIAction
object DeleteCurrentToTrash: UIAction
data class SpeechRecognize(val dontAskMore: Boolean): UIAction
object UploadCurrentToCloud: UIAction
object ReviewApp: UIAction
object ShowDevSite: UIAction
data class SelectArtist(val artist: String): UIAction

data class ParseAndExecuteVoiceCommand(val command: String): UIAction
