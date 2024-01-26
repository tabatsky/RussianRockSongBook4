package jatx.russianrocksongbook.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController

object NavControllerHolder {
    @SuppressLint("StaticFieldLeak")
    private var navController: NavHostController? = null

    private var previousDestination: NavDestination? = null

    private var onSubmitBackAction: (() -> Unit)? = null

    private var skipSubmitBackAction = 0

    private var destinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->

            //Log.e("was", previousDestination?.route ?: "null")
            //Log.e("become", destination.route ?: "null")
            //Log.e("args", arguments.toString())
            //Log.e("skip submit", skipSubmitBackAction.toString())

            val wasSongListScreen = previousDestination.isSongListScreen
            val becomeStartScreen = destination.isStartScreen

            val wasFavoriteScreen = previousDestination.isFavorite
            val wasCloudSearchScreen = previousDestination.isCloudSearchScreen
            val wasAddArtistScreen = previousDestination.isAddArtistScreen
            val wasAddSongScreen = previousDestination.isAddSongScreen
            val wasDonationScreen = previousDestination.isDonationScreen
            val wasSettingsScreen = previousDestination.isSettingsScreen

            val wasSongTextScreen = previousDestination.isSongTextScreen

            val becomeSongListScreen = destination.isSongListScreen
            val becomeFavoriteScreen = destination.isFavorite
            val becomeSongListOrFavoriteScreen = becomeSongListScreen || becomeFavoriteScreen

            val wasCloudSongTextScreen = previousDestination.isCloudSongTextScreen
            val becomeCloudSearchScreen = destination.isCloudSearchScreen

            previousDestination = destination

            var needSubmitBackAction = false

            val dontSubmitBackAction = skipSubmitBackAction > 0

            needSubmitBackAction = needSubmitBackAction || (wasSongListScreen && becomeStartScreen)

            needSubmitBackAction = needSubmitBackAction ||
                    (wasFavoriteScreen && becomeSongListScreen && !dontSubmitBackAction)

            needSubmitBackAction = needSubmitBackAction || (wasSongTextScreen && becomeSongListOrFavoriteScreen)

            needSubmitBackAction = needSubmitBackAction || (wasCloudSearchScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasAddArtistScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasAddSongScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasDonationScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasSettingsScreen && becomeSongListOrFavoriteScreen)

            needSubmitBackAction = needSubmitBackAction || (wasCloudSongTextScreen && becomeCloudSearchScreen)

            if (needSubmitBackAction) {
                this@NavControllerHolder.onSubmitBackAction?.invoke()
            }

            if (skipSubmitBackAction > 0) {
                skipSubmitBackAction -= 1
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

    fun popBackStack(dontSubmitBackAction: Boolean = false, times: Int = 1) {
        if (dontSubmitBackAction) {
            skipSubmitBackAction += times
        }
        repeat(times) {
            navController?.popBackStack()
        }
    }

    fun navigate(screenVariant: ScreenVariant) {
        navController?.navigate(screenVariant.destination) {
            launchSingleTop = true
        }
    }

    val navControllerIsNull: Boolean
        get() = navController == null
}