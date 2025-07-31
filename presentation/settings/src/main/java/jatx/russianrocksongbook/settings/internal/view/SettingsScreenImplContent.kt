package jatx.russianrocksongbook.settings.internal.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.repository.preferences.FontScale
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.settings.internal.viewmodel.ApplySettings
import jatx.russianrocksongbook.settings.internal.viewmodel.SaveSettings
import jatx.russianrocksongbook.testing.APP_BAR_TITLE
import jatx.spinner.SpinnerState

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SettingsScreenImplContent(
    valueTheme: MutableState<Theme>,
    spinnerStateTheme: MutableState<SpinnerState>,
    valueFontScale: MutableState<FontScale>,
    spinnerStateFontScale: MutableState<SpinnerState>,
    valueOrientation: MutableState<Orientation>,
    spinnerStateOrientation: MutableState<SpinnerState>,
    valueListenToMusicVariant: MutableState<ListenToMusicVariant>,
    spinnerStateListenToMusicVariant: MutableState<SpinnerState>,
    stringScrollSpeed: MutableState<String>,
    valueScrollSpeed: MutableFloatState,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val themeToSave by valueTheme
    val fontScaleToSave by valueFontScale
    val orientationToSave by valueOrientation
    val listenToMusicVariantToSave by valueListenToMusicVariant
    val scrollSpeedToSave by valueScrollSpeed

    val onSaveClick: () -> Unit = {
        submitAction(
            SaveSettings(
                themeToSave,
                fontScaleToSave.scale,
                orientationToSave,
                listenToMusicVariantToSave,
                scrollSpeedToSave
            )
        )
        submitAction(ApplySettings)
    }

    val fontSizeLabelSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.LABEL)
    val fontSizeButtonSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.BUTTON)

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
                CommonTopAppBar(
                    title = stringResource(id = R.string.title_settings),
                    titleTestTag = APP_BAR_TITLE
                )
                SettingsBodyPortrait(
                    theme = theme,
                    fontSizeLabelSp = fontSizeLabelSp,
                    fontSizeButtonSp = fontSizeButtonSp,
                    valueTheme = valueTheme,
                    spinnerStateTheme = spinnerStateTheme,
                    valueFontScale = valueFontScale,
                    spinnerStateFontScale = spinnerStateFontScale,
                    valueOrientation = valueOrientation,
                    spinnerStateOrientation = spinnerStateOrientation,
                    valueListenToMusicVariant = valueListenToMusicVariant,
                    spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant,
                    valueScrollSpeed = valueScrollSpeed,
                    stringScrollSpeed = stringScrollSpeed,
                    onSaveClick = onSaveClick
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonSideAppBar(
                    title = stringResource(id = R.string.title_settings),
                    titleTestTag = APP_BAR_TITLE
                )
                SettingsBodyLandscape(
                    theme = theme,
                    fontSizeLabelSp = fontSizeLabelSp,
                    fontSizeButtonSp = fontSizeButtonSp,
                    valueTheme = valueTheme,
                    spinnerStateTheme = spinnerStateTheme,
                    valueFontScale = valueFontScale,
                    spinnerStateFontScale = spinnerStateFontScale,
                    valueOrientation = valueOrientation,
                    spinnerStateOrientation = spinnerStateOrientation,
                    valueListenToMusicVariant = valueListenToMusicVariant,
                    spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant,
                    valueScrollSpeed = valueScrollSpeed,
                    stringScrollSpeed = stringScrollSpeed,
                    onSaveClick = onSaveClick
                )
            }
        }
    }
}