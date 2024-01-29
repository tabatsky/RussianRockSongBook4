package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.*
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.settings.internal.viewmodel.ApplySettings
import jatx.russianrocksongbook.settings.internal.viewmodel.SaveSettings
import jatx.russianrocksongbook.settings.internal.viewmodel.SettingsViewModel
import jatx.russianrocksongbook.testing.APP_BAR_TITLE

@Composable
internal fun SettingsScreenImpl() {
    val settingsViewModel = SettingsViewModel.getInstance()

    val theme = LocalAppTheme.current

    var themeToSave by settingsViewModel.valueTheme
    val onThemePositionChanged: (Int) -> Unit = {
        themeToSave = Theme.entries[it]
    }

    var fontScaleToSave by settingsViewModel.valueFontScale
    val onFontScalePositionChanged: (Int) -> Unit = {
        fontScaleToSave = FontScale.entries[it]
    }

    var defaultArtistToSave by settingsViewModel.valueDefaultArtist
    val onDefaultArtistValueChanged: (String) -> Unit = {
        defaultArtistToSave = it
    }

    var orientationToSave by settingsViewModel.valueOrientation
    val onOrientationPositionChanged: (Int) -> Unit = {
        orientationToSave = Orientation.entries[it]
    }

    var listenToMusicVariantToSave by settingsViewModel.valueListenToMusicVariant
    val onListenToMusicVariantPositionChanged: (Int) -> Unit = {
        listenToMusicVariantToSave =
            ListenToMusicVariant.entries[it]
    }

    var scrollSpeedToSave by settingsViewModel.valueScrollSpeed
    val onScrollSpeedValueChanged: (Float) -> Unit = {
        scrollSpeedToSave = it
    }

    val onSaveClick: () -> Unit = {
        settingsViewModel.submitAction(
            SaveSettings(
                themeToSave,
                fontScaleToSave.scale,
                defaultArtistToSave,
                orientationToSave,
                listenToMusicVariantToSave,
                scrollSpeedToSave
            ))
        settingsViewModel.submitAction(ApplySettings)
    }

    val fontSizeLabelSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(settingsViewModel.fontScaler, ScalePow.LABEL)
    val fontSizeButtonSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(settingsViewModel.fontScaler, ScalePow.BUTTON)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.minHeight

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
                    onThemePositionChanged = onThemePositionChanged,
                    onFontScalePositionChanged = onFontScalePositionChanged,
                    onDefaultArtistValueChanged = onDefaultArtistValueChanged,
                    onOrientationPositionChanged = onOrientationPositionChanged,
                    onListenToMusicVariantPositionChanged = onListenToMusicVariantPositionChanged,
                    onScrollSpeedValueChanged = onScrollSpeedValueChanged,
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
                    onThemePositionChanged = onThemePositionChanged,
                    onFontScalePositionChanged = onFontScalePositionChanged,
                    onDefaultArtistValueChanged = onDefaultArtistValueChanged,
                    onOrientationPositionChanged = onOrientationPositionChanged,
                    onListenToMusicVariantPositionChanged = onListenToMusicVariantPositionChanged,
                    onScrollSpeedValueChanged = onScrollSpeedValueChanged,
                    onSaveClick = onSaveClick
                )
            }
        }
    }
}
