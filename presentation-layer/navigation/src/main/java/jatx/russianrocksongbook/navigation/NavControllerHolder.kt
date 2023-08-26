package jatx.russianrocksongbook.navigation

import android.annotation.SuppressLint
import androidx.navigation.NavHostController

object NavControllerHolder {
    @SuppressLint("StaticFieldLeak")
    var navController: NavHostController? = null

    fun injectNavController(navController: NavHostController) {
        this.navController = navController
    }

    fun cleanNavController() {
        navController = null
    }
}