package jatx.russianrocksongbook.preferences.repository

import android.annotation.SuppressLint
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.repository.preferences.*
import jatx.russianrocksongbook.preferences.storage.SettingsStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
@BoundTo(supertype = SettingsRepository::class, component = SingletonComponent::class)
@SuppressLint("ApplySharedPref")
class SettingsRepositoryImpl @Inject constructor(
    private val storage: SettingsStorage
): SettingsRepository {

    override val appWasUpdated: Boolean
        get() = storage.appWasUpdated

    override fun confirmAppUpdate() {
        storage.confirmAppUpdate()
    }

    override var theme: Theme
        get() = Theme.values()[storage.theme]
        set(value) {
            storage.theme = value.ordinal
        }

    override var orientation: Orientation
        get() = Orientation.values()[storage.orientation]
        set(value) {
            storage.orientation = value.ordinal
        }


    override var listenToMusicVariant: ListenToMusicVariant
        get() = ListenToMusicVariant
            .values()[storage.listenToMusicVariant]
        set(value) {
            storage.listenToMusicVariant = value.ordinal
        }

    override var defaultArtist: String
        get() = storage.defaultArtist ?: ARTIST_KINO
        set(value) {
            storage.defaultArtist = value
        }

    override var scrollSpeed: Float
        get() = storage.scrollSpeed
        set(value) {
            storage.scrollSpeed = value
        }

    override var youtubeMusicDontAsk: Boolean
        get() = storage.youtubeMusicDontAsk
        set(value) {
            storage.youtubeMusicDontAsk = value
        }

    override var vkMusicDontAsk: Boolean
        get() = storage.vkMusicDontAsk
        set(value) {
            storage.vkMusicDontAsk = value
        }

    override var yandexMusicDontAsk: Boolean
        get() = storage.yandexMusicDontAsk
        set(value) {
            storage.yandexMusicDontAsk = value
        }

    override var voiceHelpDontAsk: Boolean
        get() = storage.voiceHelpDontAsk
        set(value) {
            storage.voiceHelpDontAsk = value
        }


    override var commonFontScale: Float
        get() = storage.commonFontScale
        set(value) {
            storage.commonFontScale = value
        }

    override val commonFontScaleEnum: FontScale
        get() = FontScale.values().find { it.scale == commonFontScale } ?: FontScale.M

    override fun getSpecificFontScale(scalePow: ScalePow) = commonFontScale.pow(scalePow.pow)
}

