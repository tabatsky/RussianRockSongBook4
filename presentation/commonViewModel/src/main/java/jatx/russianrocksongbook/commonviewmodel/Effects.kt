package jatx.russianrocksongbook.commonviewmodel

import androidx.annotation.StringRes

interface UIEffect

data class ShowToastWithText(val text: String): UIEffect
data class ShowToastWithResource(@param:StringRes val resId: Int): UIEffect