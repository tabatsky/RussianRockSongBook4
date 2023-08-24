package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.navigation.ScreenVariant

interface UIAction

object Back: UIAction
data class SelectScreen(val screenVariant: ScreenVariant): UIAction
data class AppWasUpdated(val wasUpdated: Boolean): UIAction
