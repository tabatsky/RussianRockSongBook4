package jatx.russianrocksongbook.commonsongtext.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song

data class UpdateCurrentSong(val song: Song?): UIAction
data class SelectSong(val position: Int): UIAction
object NextSong: UIAction
object PrevSong: UIAction
data class SetEditorMode(val isEditor: Boolean): UIAction
data class SetAutoPlayMode(val isAutoPlay: Boolean): UIAction
data class SaveSong(val song: Song): UIAction
data class SetFavorite(val favorite: Boolean): UIAction
object UploadCurrentToCloud: UIAction
data class UpdateSongListScrollPosition(val position: Int): UIAction
data class UpdateSongListNeedScroll(val need: Boolean): UIAction