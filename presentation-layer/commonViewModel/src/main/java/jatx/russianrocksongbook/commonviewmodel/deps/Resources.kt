package jatx.russianrocksongbook.commonviewmodel.deps

import androidx.annotation.StringRes

interface Resources {
    fun getString(@StringRes resId: Int, vararg args: Any): String
}