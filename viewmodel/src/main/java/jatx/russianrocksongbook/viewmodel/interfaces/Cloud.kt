package jatx.russianrocksongbook.viewmodel.interfaces

interface Cloud {
    fun openYandexMusicCloud(dontAskMore: Boolean)
    fun openVkMusicCloud(dontAskMore: Boolean)
    fun openYoutubeMusicCloud(dontAskMore: Boolean)
    fun sendWarningCloud(comment: String)
}