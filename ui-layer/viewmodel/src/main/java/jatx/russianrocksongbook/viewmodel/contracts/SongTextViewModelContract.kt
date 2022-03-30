package jatx.russianrocksongbook.viewmodel.contracts

interface SongTextViewModelContract {
    fun openYandexMusicImpl(dontAskMore: Boolean)
    fun openVkMusicImpl(dontAskMore: Boolean)
    fun openYoutubeMusicImpl(dontAskMore: Boolean)
    fun sendWarningImpl(comment: String)
}