package jatx.russianrocksongbook.navigation

import kotlinx.serialization.Serializable
import kotlin.random.Random

val rnd = Random(42)

sealed interface ScreenVariant {

    @Serializable
    data object Start: ScreenVariant

    @Serializable
    data class SongList(
        val artist: String,
        val isBackFromSomeScreen: Boolean = false
    ): ScreenVariant

    @Serializable
    data class Favorite(
        val isBackFromSomeScreen: Boolean = false
    ): ScreenVariant

    @Serializable
    data class SongText(
        val position: Int,
        val randomKey: Int = rnd.nextInt()
        ): ScreenVariant

    @Serializable
    data class SongTextByArtistAndTitle(
        val artist: String,
        val title: String
        ): ScreenVariant

    @Serializable
    data class CloudSearch(
        val randomKey: Int = rnd.nextInt(),
        val isBackFromSong: Boolean = false
    ): ScreenVariant

    @Serializable
    data class CloudSongText(
        val position: Int
    ): ScreenVariant

    @Serializable
    data class TextSearchList(
        val randomKey: Int = rnd.nextInt(),
        val isBackFromSong: Boolean = false
    ): ScreenVariant

    @Serializable
    data class TextSearchSongText(
        val position: Int,
        val randomKey: Int = rnd.nextInt()
    ): ScreenVariant

    @Serializable
    data object AddArtist: ScreenVariant

    @Serializable
    data object AddSong: ScreenVariant

    @Serializable
    data object Donation: ScreenVariant

    @Serializable
    data object Settings: ScreenVariant
}
