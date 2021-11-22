package jatx.russianrocksongbook.viewmodel.interfaces

interface Local {
    fun openYandexMusicLocal(dontAskMore: Boolean)
    fun openVkMusicLocal(dontAskMore: Boolean)
    fun openYoutubeMusicLocal(dontAskMore: Boolean)
    fun sendWarningLocal(comment: String)
}