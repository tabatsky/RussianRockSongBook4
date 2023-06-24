package jatx.russianrocksongbook.viewmodel.navigation

import androidx.navigation.NavHostController
import java.lang.IllegalStateException

object NavControllerHolder {
    private var _navController: NavHostController? = null
    val navController: NavHostController
        get() = _navController ?: throw IllegalStateException("nav controller is null")

    fun injectNavController(navController: NavHostController) {
        _navController = navController
    }

    fun cleanNavController() {
        _navController = null
    }
}