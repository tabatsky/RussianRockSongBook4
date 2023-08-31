package jatx.russianrocksongbook.settings.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.Theme

data class SaveSettings(
    val theme: Theme,
    val fontScale: Float,
    val defaultArtist: String,
    val orientation: Orientation,
    val listenToMusicVariant: ListenToMusicVariant,
    val scrollSpeed: Float
): UIAction

object RestartApp: UIAction