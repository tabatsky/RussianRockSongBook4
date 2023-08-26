package jatx.russianrocksongbook.commonviewmodel.contracts

interface MusicOpener {
    fun openVkMusicImpl(dontAskMore: Boolean)
    fun openYandexMusicImpl(dontAskMore: Boolean)
    fun openYoutubeMusicImpl(dontAskMore: Boolean)
}