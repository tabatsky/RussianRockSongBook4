package jatx.russianrocksongbook.viewmodel

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Actions @Inject constructor() {
    var onOpenYandexMusic: (String) -> Unit = {}
    var onOpenVkMusic: (String) -> Unit = {}
    var onOpenYoutubeMusic: (String) -> Unit = {}
    var onShowDevSite: () -> Unit = {}
    var onReviewApp: () -> Unit = {}
    var onRestartApp: () -> Unit = {}
    var onAddSongsFromDir: () -> Unit = {}
    var onPurchaseItem: (String) -> Unit = {}
}