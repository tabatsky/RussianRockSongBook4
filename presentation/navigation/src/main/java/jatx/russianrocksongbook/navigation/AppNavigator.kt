package jatx.russianrocksongbook.navigation

import android.annotation.SuppressLint
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController

object AppNavigator {
    @SuppressLint("StaticFieldLeak")
    private var navController: NavHostController? = null

    private var previousDestination: NavDestination? = null

    private var onSubmitBackAction: (() -> Unit)? = null

    private var skipSubmitBackAction = 0

    private var skipOnce = false

    private var destinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            val wasSongListScreen = previousDestination.isSongListScreen
            val becomeStartScreen = destination.isStartScreen

            val wasFavoriteScreen = previousDestination.isFavorite
            val wasCloudSearchScreen = previousDestination.isCloudSearchScreen
            val wasTextSearchListScreen = previousDestination.isTextSearchListScreen
            val wasDonationScreen = previousDestination.isDonationScreen
            val wasSettingsScreen = previousDestination.isSettingsScreen
            val wasAddArtistScreen = previousDestination.isAddArtistScreen
            val wasAddSongScreen = previousDestination.isAddSongScreen

            val wasSongTextScreen = previousDestination.isSongTextScreen

            val becomeSongListScreen = destination.isSongListScreen
            val becomeFavoriteScreen = destination.isFavorite
            val becomeSongListOrFavoriteScreen = becomeSongListScreen || becomeFavoriteScreen

            val wasCloudSongTextScreen = previousDestination.isCloudSongTextScreen
            val becomeCloudSearchScreen = destination.isCloudSearchScreen

            val wasTextSearchSongTextScreen = previousDestination.isTextSearchSongTextScreen
            val becomeTextSearchListScreen = destination.isTextSearchListScreen

            var needSubmitBackAction = false

            val dontSubmitBackAction = skipSubmitBackAction > 0

            needSubmitBackAction = needSubmitBackAction || (wasSongListScreen && becomeStartScreen)

            needSubmitBackAction = needSubmitBackAction || (wasFavoriteScreen && becomeSongListScreen)

            needSubmitBackAction = needSubmitBackAction || (wasSongTextScreen && becomeSongListOrFavoriteScreen)

            needSubmitBackAction = needSubmitBackAction || (wasCloudSearchScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasTextSearchListScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasDonationScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasSettingsScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasAddArtistScreen && becomeSongListOrFavoriteScreen)
            needSubmitBackAction = needSubmitBackAction || (wasAddSongScreen && becomeSongListOrFavoriteScreen)

            needSubmitBackAction = needSubmitBackAction || (wasCloudSongTextScreen && becomeCloudSearchScreen)
            needSubmitBackAction = needSubmitBackAction || (wasTextSearchSongTextScreen && becomeTextSearchListScreen)

            needSubmitBackAction = needSubmitBackAction && !dontSubmitBackAction

            if (needSubmitBackAction && !skipOnce) {
                this@AppNavigator.onSubmitBackAction?.invoke()
            }

            if (skipSubmitBackAction > 0) {
                skipSubmitBackAction -= 1
            }

            skipOnce = false

            previousDestination = destination
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

    fun popBackStack(
        dontSubmitBackAction: Boolean = false,
        skipOnce: Boolean = false,
        times: Int = 1
    ) {
        if (dontSubmitBackAction) {
            skipSubmitBackAction += times
        }
        this.skipOnce = skipOnce
        repeat(times) {
            navController?.popBackStack()
        }
    }

    fun navigate(screenVariant: ScreenVariant) {
        navController?.navigate(screenVariant.route) {}
    }

    val navControllerIsNull: Boolean
        get() = navController == null
}