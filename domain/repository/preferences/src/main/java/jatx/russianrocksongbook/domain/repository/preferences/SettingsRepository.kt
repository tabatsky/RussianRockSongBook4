package jatx.russianrocksongbook.domain.repository.preferences

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.StateFlow

const val ARTIST_KINO = "Кино"

interface SettingsRepository {
    val appWasUpdated: Boolean
    var theme: Theme
    var orientation: Orientation
    var listenToMusicVariant: ListenToMusicVariant
    var defaultArtist: String
    var scrollSpeed: Float
    var youtubeMusicDontAsk: Boolean
    var vkMusicDontAsk: Boolean
    var yandexMusicDontAsk: Boolean
    var voiceHelpDontAsk: Boolean
    var commonFontScale: Float
    val commonFontScaleEnum: FontScale
    val fontScaler: FontScaler

    fun confirmAppUpdate()
    fun getSpecificFontScale(scalePow: ScalePow): Float

    val youtubeMusicDontAskState: StateFlow<Boolean>
    val vkMusicDontAskState: StateFlow<Boolean>
    val yandexMusicDontAskState: StateFlow<Boolean>
    val voiceHelpDontAskState: StateFlow<Boolean>
}

val colorLightYellow = Color(0xFFFFFFBB)
val colorBlack = Color(0xFF000000)
val colorDarkYellow = Color(0xFF777755)

enum class Theme(
    val colorMain: Color,
    val colorBg: Color,
    val colorCommon: Color
) {
    DARK(
        colorLightYellow,
        colorBlack,
        colorDarkYellow
    ), LIGHT(
        colorBlack,
        colorLightYellow,
        colorDarkYellow
    )
}

enum class Orientation {
    RANDOM, PORTRAIT, LANDSCAPE
}

enum class ListenToMusicVariant {
    YANDEX_AND_YOUTUBE,
    VK_AND_YOUTUBE,
    YANDEX_AND_VK;

    val isYandex: Boolean
        get() = (this == YANDEX_AND_YOUTUBE).or(this == YANDEX_AND_VK)
    val isYoutube: Boolean
        get() = (this == YANDEX_AND_YOUTUBE).or(this == VK_AND_YOUTUBE)
    val isVk: Boolean
        get() = (this == VK_AND_YOUTUBE).or(this == YANDEX_AND_VK)
}

enum class ScalePow(
    val pow: Float
) {
    TEXT(0.7f),
    LABEL(0.5f),
    MENU(0.6f),
    BUTTON(0.3f)
}

enum class FontScale(
    val scale: Float
) {
    XS(0.5f), S(0.75f), M(1.0f), L(1.5f), XL(2.0f)
}

interface FontScaler {
    fun getSpecificFontScale(scalePow: ScalePow): Float
}