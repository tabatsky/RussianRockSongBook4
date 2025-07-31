package jatx.russianrocksongbook.addsong.internal.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.addsong.R
import jatx.russianrocksongbook.addsong.internal.viewmodel.AddSongState
import jatx.russianrocksongbook.addsong.internal.viewmodel.HideUploadOfferForSong
import jatx.russianrocksongbook.addsong.internal.viewmodel.Reset
import jatx.russianrocksongbook.addsong.internal.viewmodel.ShowNewSong
import jatx.russianrocksongbook.addsong.internal.viewmodel.UploadNewToCloud
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.commonviewmodel.UIEffect

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AddSongScreenImplContent(
    artistState: MutableState<String>,
    titleState: MutableState<String>,
    textState: MutableState<String>,
    addSongStateState: State<AddSongState>,
    submitAction: (UIAction) -> Unit,
    submitEffect: (UIEffect) -> Unit
) {
    var screenInitDone by rememberSaveable { mutableStateOf(false) }

    if (!screenInitDone) {
        LaunchedEffect(Unit) {
            screenInitDone = true
            submitAction(Reset)
        }
    }

    val addSongState by addSongStateState
    val showUploadDialog = addSongState.showUploadDialogForSong

    val theme = LocalAppTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val W = configuration.screenWidthDp.dp
        val H = configuration.screenHeightDp.dp

        if (W < H) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonTopAppBar(title = stringResource(id = R.string.title_add_song))
                AddSongBody(
                    artistState = artistState,
                    titleState = titleState,
                    textState = textState,
                    submitAction = submitAction,
                    submitEffect = submitEffect
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonSideAppBar(title = stringResource(id = R.string.title_add_song))
                AddSongBody(
                    artistState = artistState,
                    titleState = titleState,
                    textState = textState,
                    submitAction = submitAction,
                    submitEffect = submitEffect
                )
            }
        }

        if (showUploadDialog) {
            UploadDialog(
                onConfirm = {
                    submitAction(UploadNewToCloud)
                },
                onDecline = {
                    submitAction(ShowNewSong)
                },
                onDismiss = {
                    submitAction(HideUploadOfferForSong)
                }
            )
        }
    }
}