package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy

data class PerformTextSearch(val searchFor: String, val orderBy: TextSearchOrderBy): UIAction
data class UpdateSearchFor(val searchFor: String): UIAction
data class UpdateOrderBy(val orderBy: TextSearchOrderBy): UIAction
data class UpdateCurrentSongCount(val count: Int): UIAction
data class UpdateCurrentSong(val cloudSong: Song?): UIAction
data class SelectSong(val position: Int): UIAction
data class UpdateSongListScrollPosition(val position: Int): UIAction
data class UpdateSongListNeedScroll(val needScroll: Boolean): UIAction
object NextSong: UIAction
object PrevSong: UIAction
data class SetEditorMode(val isEditor: Boolean): UIAction
data class SetAutoPlayMode(val isAutoPlay: Boolean): UIAction
data class SaveSong(val song: Song): UIAction
data class SetFavorite(val favorite: Boolean): UIAction
object UploadCurrentToCloud: UIAction
object DeleteCurrentToTrash: UIAction