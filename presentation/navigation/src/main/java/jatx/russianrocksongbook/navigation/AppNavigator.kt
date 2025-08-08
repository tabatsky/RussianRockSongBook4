package jatx.russianrocksongbook.navigation

import android.util.Log
import androidx.navigation3.runtime.NavBackStack
import jatx.russianrocksongbook.commonappstate.AppState
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import kotlin.properties.Delegates.notNull

class AppNavigator() {
    private var _backStack by notNull<NavBackStack>()
    val backStack: NavBackStack
        get() = _backStack

    val currentScreenVariant: ScreenVariant
        get() = backStack.lastOrNull() as? ScreenVariant
            ?: throw IllegalStateException("incorrect stack")

    private var previousScreenVariant: ScreenVariant? = null

    private var getAppState: (() -> AppState)? = null
    private var onChangeCurrentScreenVariant: ((ScreenVariant) -> Unit)? = null

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
            backByDestinationChangedListener()
        }

        if (skipSubmitBackAction > 0) {
            skipSubmitBackAction -= 1
        }

        skipOnce = false

        previousScreenVariant = screenVariant
    }

    fun backByUser() = pop()

    fun selectScreen(newScreenVariant: ScreenVariant) {
        val isSongByArtistAndTitle = currentScreenVariant is SongTextByArtistAndTitleScreenVariant
        val isStart = currentScreenVariant is StartScreenVariant
        val isFavorite = currentScreenVariant is FavoriteScreenVariant
        val becomeSongList = newScreenVariant is SongListScreenVariant
        val isSongList = currentScreenVariant is SongListScreenVariant
        val becomeFavorite = newScreenVariant is FavoriteScreenVariant
        val becomeSongByArtistAndTitle = newScreenVariant is SongTextByArtistAndTitleScreenVariant

        val isAddSong = currentScreenVariant is AddSongScreenVariant
        val isAddArtist = currentScreenVariant is AddArtistScreenVariant

        val isSongText = currentScreenVariant is SongTextScreenVariant
        val becomeSongText = newScreenVariant is SongTextScreenVariant
        val isCloudSongText = currentScreenVariant is CloudSongTextScreenVariant
        val becomeCloudSongText = newScreenVariant is CloudSongTextScreenVariant
        val isTextSearchSongText = currentScreenVariant is TextSearchSongTextScreenVariant
        val becomeTextSearchSongText = newScreenVariant is TextSearchSongTextScreenVariant

        val artistNow = (currentScreenVariant as? SongListScreenVariant)?.artist
        val artistBecome = (newScreenVariant as? SongListScreenVariant)?.artist

        val isBackNow = (currentScreenVariant as? SongListScreenVariant)?.isBackFromSomeScreen
        val isBackBecome = (newScreenVariant as? SongListScreenVariant)?.isBackFromSomeScreen

        val needToPopTwice = isSongByArtistAndTitle || isAddArtist && becomeSongList
        val needToReturn = isSongList && becomeSongList &&
                (artistNow == artistBecome) &&
                (isBackNow == isBackBecome)

        var needToPop = false
        needToPop = needToPop || isStart && becomeSongList
        needToPop = needToPop || isFavorite && becomeSongList
        needToPop = needToPop || isSongList && becomeFavorite
        needToPop = needToPop || isAddSong && becomeSongByArtistAndTitle

        var needToPopWithSkippingBackOnce = false
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || (isSongList || isFavorite) && (becomeSongList || becomeFavorite)
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || isSongText && becomeSongText
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || isCloudSongText && becomeCloudSongText
        needToPopWithSkippingBackOnce = needToPopWithSkippingBackOnce || isTextSearchSongText && becomeTextSearchSongText

        if (needToPopTwice) {
            pop(dontSubmitBackAction = true, times = 2)
        } else if (needToPop) {
            pop(dontSubmitBackAction = true)
        } else if (needToReturn) {
            return
        } else if (needToPopWithSkippingBackOnce) {
            pop(skipOnce = true)
        }

        onChangeCurrentScreenVariant?.invoke(newScreenVariant)

        val isBackFromCertainScreen = (newScreenVariant as? SongListScreenVariant)?.isBackFromSomeScreen
                ?: (newScreenVariant as? FavoriteScreenVariant)?.isBackFromSomeScreen
                ?: (newScreenVariant as? CloudSearchScreenVariant)?.isBackFromSong
                ?: (newScreenVariant as? TextSearchListScreenVariant)?.isBackFromSong
                ?: false
                && !isAddArtist // already popped
                && (backStack.lastOrNull()?.javaClass == newScreenVariant.javaClass)

        if (!isBackFromCertainScreen) {
            push(newScreenVariant)
        } else {
            replace(newScreenVariant)
        }

        Log.e("navigated", newScreenVariant.toString())
    }

    fun injectCallbacks(
        getAppState: (() -> AppState),
        onChangeCurrentScreenVariant: (ScreenVariant) -> Unit
    ) {
        this.getAppState = getAppState
        this.onChangeCurrentScreenVariant = onChangeCurrentScreenVariant
    }

    fun updateBackStack(backStack: NavBackStack) {
        _backStack = backStack
    }

    private fun pop(
        dontSubmitBackAction: Boolean = false,
        skipOnce: Boolean = false,
        times: Int = 1
    ) {
        val screenVariant = this.backStack.removeLastOrNull() as? ScreenVariant
        if (dontSubmitBackAction) {
            skipSubmitBackAction += times
        }
        this.skipOnce = skipOnce
        repeat(times) {
            this.screenChangedListener(screenVariant)
        }
    }

    private fun push(screenVariant: ScreenVariant) {
        this.backStack.add(screenVariant)
        this.screenChangedListener(screenVariant)
    }

    private fun replace(screenVariant: ScreenVariant) {
        this.backStack.removeLastOrNull()
        this.backStack.add(screenVariant)
        this.screenChangedListener(screenVariant)
    }

    private fun backByDestinationChangedListener() {
        Log.e("back by", "destination listener")
        getAppState?.invoke()?.let {
            with(it) {
                Log.e("back from", currentScreenVariant.toString())
                when (currentScreenVariant) {
                    is SongListScreenVariant,
                    is FavoriteScreenVariant,
                    is StartScreenVariant -> {
                        doNothing()
                    }

                    is SongTextScreenVariant -> {
                        if (currentArtist != ARTIST_FAVORITE) {
                            selectScreen(
                                SongListScreenVariant(
                                    artist = currentArtist,
                                    isBackFromSomeScreen = true
                                )
                            )
                        } else {
                            selectScreen(FavoriteScreenVariant(isBackFromSomeScreen = true))
                        }
                    }

                    is CloudSongTextScreenVariant -> {
                        selectScreen(
                            CloudSearchScreenVariant(
                                randomKey = lastRandomKey,
                                isBackFromSong = true
                            )
                        )
                    }

                    is TextSearchSongTextScreenVariant -> {
                        selectScreen(
                            TextSearchListScreenVariant(
                                randomKey = lastRandomKey,
                                isBackFromSong = true
                            )
                        )
                    }

                    else -> {
                        if (currentArtist != ARTIST_FAVORITE) {
                            selectScreen(
                                SongListScreenVariant(
                                    artist = currentArtist,
                                    isBackFromSomeScreen = true
                                )
                            )
                        } else {
                            selectScreen(FavoriteScreenVariant(isBackFromSomeScreen = true))
                        }
                    }
                }
            }
        }
    }

    private fun doNothing() = Unit
}