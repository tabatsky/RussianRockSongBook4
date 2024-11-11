package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.navigation.ScreenVariant

interface UIAction

data class Back(val byDestinationChangedListener: Boolean = false): UIAction
data class SelectScreen(val screenVariant: ScreenVariant): UIAction
data class AppWasUpdated(val wasUpdated: Boolean): UIAction
data class OpenVkMusic(val dontAskMore: Boolean): UIAction
data class OpenYandexMusic(val dontAskMore: Boolean): UIAction
data class OpenYoutubeMusic(val dontAskMore: Boolean): UIAction
data class SendWarning(val comment: String): UIAction
data class ShowSongs(val artist: String, val songTitleToPass: String? = null): UIAction

data class UpdateCurrentSong(val song: Song?): UIAction
data class SelectSong(val position: Int): UIAction
object NextSong: UIAction
object PrevSong: UIAction
data class SetEditorMode(val isEditor: Boolean): UIAction
data class SetAutoPlayMode(val isAutoPlay: Boolean): UIAction
data class SaveSong(val song: Song): UIAction
data class SetFavorite(val favorite: Boolean): UIAction
object UploadCurrentToCloud: UIAction
object DeleteCurrentToTrash: UIAction