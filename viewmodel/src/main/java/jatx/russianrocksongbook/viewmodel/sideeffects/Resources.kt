package jatx.russianrocksongbook.viewmodel.sideeffects

import androidx.annotation.StringRes

interface Resources {
    fun getString(@StringRes resId: Int, vararg args: Any): String
}