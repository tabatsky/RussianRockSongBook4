package jatx.russianrocksongbook.commonsongtext.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song

data class UpdateCurrentSong(val song: Song?): UIAction
data class UpdateLastRandomKey(val randomKey: Int): UIAction
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

data class UpdateShowYandexDialog(val needShow: Boolean): UIAction
data class UpdateShowVkDialog(val needShow: Boolean): UIAction
data class UpdateShowYoutubeDialog(val needShow: Boolean): UIAction
data class UpdateShowUploadDialog(val needShow: Boolean): UIAction
data class UpdateShowDeleteToTrashDialog(val needShow: Boolean): UIAction
data class UpdateShowWarningDialog(val needShow: Boolean): UIAction
data class UpdateShowChordDialog(
    val needShow: Boolean,
    val selectedChord: String = ""
): UIAction