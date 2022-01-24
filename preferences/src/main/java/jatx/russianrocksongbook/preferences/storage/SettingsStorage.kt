package jatx.russianrocksongbook.preferences.storage

interface SettingsStorage {
    val appWasUpdated: Boolean
    var theme: Int
    var orientation: Int
    var listenToMusicVariant: Int
    var defaultArtist: String?
    var scrollSpeed: Float
    var youtubeMusicDontAsk: Boolean
    var vkMusicDontAsk: Boolean
    var yandexMusicDontAsk: Boolean
    var voiceHelpDontAsk: Boolean
    var commonFontScale: Float

    fun confirmAppUpdate()
}