package jatx.russianrocksongbook.viewmodel.contracts

interface SongTextViewModelContract {
    fun openVkMusicImpl(dontAskMore: Boolean)
    fun openYandexMusicImpl(dontAskMore: Boolean)
    fun openYoutubeMusicImpl(dontAskMore: Boolean)
    fun sendWarningImpl(comment: String)
}