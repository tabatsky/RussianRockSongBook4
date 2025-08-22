package jatx.russianrocksongbook.preferences.repository

import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.repository.preferences.*
import jatx.russianrocksongbook.preferences.storage.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
@BoundTo(supertype = SettingsRepository::class, component = SingletonComponent::class)
class SettingsRepositoryImpl @Inject constructor(
    private val settingsStorage: SettingsStorage
): SettingsRepository {

    override val appWasUpdated: Boolean
        get() = settingsStorage.appWasUpdated

    override fun confirmAppUpdate() = settingsStorage.confirmAppUpdate()

    override var theme: Theme
        get() = Theme.entries[settingsStorage.theme]
        set(value) {
            settingsStorage.theme = value.ordinal
        }

    override var orientation: Orientation
        get() = Orientation.entries[settingsStorage.orientation]
        set(value) {
            settingsStorage.orientation = value.ordinal
        }

    override var listenToMusicVariant: ListenToMusicVariant
        get() = ListenToMusicVariant
            .entries[settingsStorage.listenToMusicVariant]
        set(value) {
            settingsStorage.listenToMusicVariant = value.ordinal
        }

    override var defaultArtist: String
        get() = settingsStorage.defaultArtist
        set(value) {
            settingsStorage.defaultArtist = value
        }

    override var scrollSpeed: Float
        get() = settingsStorage.scrollSpeed
        set(value) {
            settingsStorage.scrollSpeed = value
        }

    override var youtubeMusicDontAsk: Boolean
        get() = settingsStorage.youtubeMusicDontAsk
        set(value) {
            settingsStorage.youtubeMusicDontAsk = value
            _youtubeMusicDontAskState.value = value
        }

    override var vkMusicDontAsk: Boolean
        get() = settingsStorage.vkMusicDontAsk
        set(value) {
            settingsStorage.vkMusicDontAsk = value
            _vkMusicDontAskState.value = value
        }

    override var yandexMusicDontAsk: Boolean
        get() = settingsStorage.yandexMusicDontAsk
        set(value) {
            settingsStorage.yandexMusicDontAsk = value
            _yandexMusicDontAskState.value = value
        }

    override var voiceHelpDontAsk: Boolean
        get() = settingsStorage.voiceHelpDontAsk
        set(value) {
            settingsStorage.voiceHelpDontAsk = value
            _voiceHelpDontAskState.value = value
        }


    override var commonFontScale: Float
        get() = settingsStorage.commonFontScale
        set(value) {
            settingsStorage.commonFontScale = value
        }

    override val commonFontScaleEnum: FontScale
        get() = commonFontScale.let { scale ->
            FontScale.entries.find { it.scale == scale }
        } ?: FontScale.S

    override val fontScaler: FontScaler
        get() = FontScalerImpl(commonFontScale)

    override fun getSpecificFontScale(scalePow: ScalePow) = commonFontScale.pow(scalePow.pow)

    override val youtubeMusicDontAskState: StateFlow<Boolean>
        get() = _youtubeMusicDontAskState
    override val vkMusicDontAskState: StateFlow<Boolean>
        get() = _vkMusicDontAskState
    override val yandexMusicDontAskState: StateFlow<Boolean>
        get() = _yandexMusicDontAskState
    override val voiceHelpDontAskState: StateFlow<Boolean>
        get() = _voiceHelpDontAskState

    private val _youtubeMusicDontAskState by lazy {
        MutableStateFlow(youtubeMusicDontAsk)
    }
    private val _vkMusicDontAskState by lazy {
        MutableStateFlow(vkMusicDontAsk)
    }
    private val _yandexMusicDontAskState by lazy {
        MutableStateFlow(yandexMusicDontAsk)
    }
    private val _voiceHelpDontAskState by lazy {
        MutableStateFlow(voiceHelpDontAsk)
    }
}

class FontScalerImpl(private val commonFontScale: Float): FontScaler {
    override fun getSpecificFontScale(scalePow: ScalePow) = commonFontScale.pow(scalePow.pow)
}
