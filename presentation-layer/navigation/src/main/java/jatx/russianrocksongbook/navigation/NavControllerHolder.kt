package jatx.russianrocksongbook.navigation

import android.annotation.SuppressLint
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController

object NavControllerHolder {
    @SuppressLint("StaticFieldLeak")
    var navController: NavHostController? = null
        private set

    private var previousDestination: NavDestination? = null

    private var onSubmitBackAction: (() -> Unit)? = null

    private var destinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            val wasSongListScreen = previousDestination?.route?.isSongListScreen ?: false
            val becomeStartScreen = destination.route?.isStartScreen ?: false

            val wasSongTextScreen = previousDestination?.route?.isSongTextScreen ?: false
            val becomeSongListScreen = destination.route?.isSongListScreen ?: false

            previousDestination = destination

            var needSubmitBackAction = false
            needSubmitBackAction = needSubmitBackAction || (wasSongListScreen && becomeStartScreen)
            needSubmitBackAction = needSubmitBackAction || (wasSongTextScreen && becomeSongListScreen)

            if (needSubmitBackAction) {
                this@NavControllerHolder.onSubmitBackAction?.invoke()
            }
        }

    fun injectNavController(navController: NavHostController, onSubmitBackAction: (() -> Unit)) {
        this.navController = navController
        this.navController?.addOnDestinationChangedListener(destinationChangedListener)
        this.onSubmitBackAction = onSubmitBackAction
    }

    fun cleanNavController() {
        this.onSubmitBackAction = null
        this.navController?.removeOnDestinationChangedListener(destinationChangedListener)
        this.navController = null
    }
}