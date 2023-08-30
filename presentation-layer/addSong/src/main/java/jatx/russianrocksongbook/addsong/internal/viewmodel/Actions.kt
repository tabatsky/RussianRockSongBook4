package jatx.russianrocksongbook.addsong.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction

object Reset: UIAction
object HideUploadOfferForSong: UIAction
data class AddSongToRepo(val artist: String, val title: String, val text: String): UIAction
object UploadNewToCloud: UIAction
object ShowNewSong: UIAction