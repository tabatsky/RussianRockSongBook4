package jatx.russianrocksongbook.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlin.random.Random

val rnd = Random(42)

@Serializable
sealed class ScreenVariant: NavKey

@Serializable
data object EmptyScreenVariant: ScreenVariant()

@Serializable
data object StartScreenVariant: ScreenVariant()

@Serializable
data class SongListScreenVariant(
    val artist: String,
    val isBackFromSomeScreen: Boolean = false
): ScreenVariant()

@Serializable
data class FavoriteScreenVariant(
    val isBackFromSomeScreen: Boolean = false
): ScreenVariant()

@Serializable
data class SongTextScreenVariant(
    val position: Int,
    val randomKey: Int = rnd.nextInt()
): ScreenVariant()

@Serializable
data class SongTextByArtistAndTitleScreenVariant(
    val artist: String,
    val title: String
): ScreenVariant()

@Serializable
data class CloudSearchScreenVariant(
    val randomKey: Int = rnd.nextInt(),
    val isBackFromSong: Boolean = false
): ScreenVariant()

@Serializable
data class CloudSongTextScreenVariant(
    val position: Int
): ScreenVariant()

@Serializable
data class TextSearchListScreenVariant(
    val randomKey: Int = rnd.nextInt(),
    val isBackFromSong: Boolean = false
): ScreenVariant()

@Serializable
data class TextSearchSongTextScreenVariant(
    val position: Int,
    val randomKey: Int = rnd.nextInt()
): ScreenVariant()

@Serializable
data object AddArtistScreenVariant: ScreenVariant()

@Serializable
data object AddSongScreenVariant: ScreenVariant()

@Serializable
data object DonationScreenVariant: ScreenVariant()

@Serializable
data object SettingsScreenVariant: ScreenVariant()
