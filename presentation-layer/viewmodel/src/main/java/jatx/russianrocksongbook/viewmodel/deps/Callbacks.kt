package jatx.russianrocksongbook.viewmodel.deps

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Callbacks @Inject constructor() {
    var onOpenYandexMusic: (String) -> Unit = {}
    var onOpenVkMusic: (String) -> Unit = {}
    var onOpenYoutubeMusic: (String) -> Unit = {}
    var onShowDevSite: () -> Unit = {}
    var onReviewApp: () -> Unit = {}
    var onRestartApp: () -> Unit = {}
    var onAddSongsFromDir: () -> Unit = {}
    var onPurchaseItem: (String) -> Unit = {}
    var onArtistSelected: (String) -> Unit = {}
    var onSongByArtistAndTitleSelected: (String, String) -> Unit =
        { _, _ -> }
    var onSpeechRecognize: () -> Unit = {}
    var onFinish: () -> Unit = {}
}