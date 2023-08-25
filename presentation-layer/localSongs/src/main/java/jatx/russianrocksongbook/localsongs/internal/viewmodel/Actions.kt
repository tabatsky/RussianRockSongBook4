package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song

data class UpdateCurrentSong(val song: Song?): UIAction
data class UpdateScrollPosition(val position: Int): UIAction
data class UpdateNeedScroll(val need: Boolean): UIAction
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
data class ShowSongs(val artist: String, val passToSongWithTitle: String? = null): UIAction
data class SelectArtist(val artist: String): UIAction
