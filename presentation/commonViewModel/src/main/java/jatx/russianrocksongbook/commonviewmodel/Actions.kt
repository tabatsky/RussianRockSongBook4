package jatx.russianrocksongbook.commonviewmodel

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