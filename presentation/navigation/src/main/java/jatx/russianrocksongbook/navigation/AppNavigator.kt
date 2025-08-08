package jatx.russianrocksongbook.navigation

import androidx.navigation3.runtime.NavBackStack
import kotlin.properties.Delegates.notNull

class AppNavigator() {
    private var _backStack by notNull<NavBackStack>()
    val backStack: NavBackStack
        get() = _backStack

    private var previousScreenVariant: ScreenVariant? = null

    private var onSubmitBackAction: (() -> Unit)? = null

    private var skipSubmitBackAction = 0

    private var skipOnce = false

    private var screenChangedListener: (ScreenVariant?) -> Unit = { screenVariant ->
        val wasCloudSearchScreen = previousScreenVariant.isCloudSearchScreen
        val wasTextSearchListScreen = previousScreenVariant.isTextSearchListScreen
        val wasDonationScreen = previousScreenVariant.isDonationScreen
        val wasSettingsScreen = previousScreenVariant.isSettingsScreen
        val wasAddArtistScreen = previousScreenVariant.isAddArtistScreen
        val wasAddSongScreen = previousScreenVariant.isAddSongScreen

        val wasSongTextScreen = previousScreenVariant.isSongTextScreen

        val becomeSongListScreen = screenVariant.isSongListScreen
        val becomeFavoriteScreen = screenVariant.isFavoriteScreen
        val becomeSongListOrFavoriteScreen = becomeSongListScreen || becomeFavoriteScreen

        val wasCloudSongTextScreen = previousScreenVariant.isCloudSongTextScreen
        val becomeCloudSearchScreen = screenVariant.isCloudSearchScreen

        val wasTextSearchSongTextScreen = previousScreenVariant.isTextSearchSongTextScreen
        val becomeTextSearchListScreen = screenVariant.isTextSearchListScreen

        var needSubmitBackAction = false

        val dontSubmitBackAction = skipSubmitBackAction > 0

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

        previousScreenVariant = screenVariant
    }

    fun inject(onSubmitBackAction: (() -> Unit)) {
        this.onSubmitBackAction = onSubmitBackAction
    }

    fun pop(
        dontSubmitBackAction: Boolean = false,
        skipOnce: Boolean = false,
        times: Int = 1
    ) {
        this.backStack.removeLastOrNull()
        if (dontSubmitBackAction) {
            skipSubmitBackAction += times
        }
        this.skipOnce = skipOnce
        val screenVariant = this.backStack.lastOrNull() as? ScreenVariant
        repeat(times) {
            this.screenChangedListener(screenVariant)
        }
    }

    fun push(screenVariant: ScreenVariant) {
        this.backStack.add(screenVariant)
        this.screenChangedListener(screenVariant)
    }

    fun replace(screenVariant: ScreenVariant) {
        this.backStack.removeLastOrNull()
        this.backStack.add(screenVariant)
        this.screenChangedListener(screenVariant)
    }

    fun updateBackStack(backStack: NavBackStack) {
        _backStack = backStack
    }
}