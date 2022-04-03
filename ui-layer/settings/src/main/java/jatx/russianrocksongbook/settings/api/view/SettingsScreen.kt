package jatx.russianrocksongbook.settings.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.domain.repository.preferences.*
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.settings.internal.view.SettingsBodyLandscape
import jatx.russianrocksongbook.settings.internal.view.SettingsBodyPortrait
import jatx.russianrocksongbook.settings.internal.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val settingsViewModel: SettingsViewModel = viewModel()

    val theme = settingsViewModel.settings.theme
    val settings = settingsViewModel.settings

    var themeToSave by remember { mutableStateOf(theme) }
    val onThemePositionChanged: (Int) -> Unit = {
        themeToSave = Theme.values()[it]
    }

    var fontScaleToSave by remember { mutableStateOf(settings.commonFontScaleEnum) }
    val onFontScalePositionChanged: (Int) -> Unit = {
        fontScaleToSave = FontScale.values()[it]
    }

    var defaultArtistToSave by remember { mutableStateOf(settings.defaultArtist) }
    val onDefaultArtistValueChanged: (String) -> Unit = {
        defaultArtistToSave = it
    }

    var orientationToSave by remember { mutableStateOf(settings.orientation) }
    val onOrientationPositionChanged: (Int) -> Unit = {
        orientationToSave = Orientation.values()[it]
    }

    var listenToMusicVariantToSave by remember { mutableStateOf(settings.listenToMusicVariant) }
    val onListenToMusicVariantPositionChanged: (Int) -> Unit = {
        listenToMusicVariantToSave =
            ListenToMusicVariant.values()[it]
    }

    var scrollSpeedToSave by remember { mutableStateOf(settings.scrollSpeed) }
    val onScrollSpeedValueChanged: (Float) -> Unit = {
        scrollSpeedToSave = it
    }

    val onSaveClick: () -> Unit = {
        settings.theme = themeToSave
        settings.commonFontScale = fontScaleToSave.scale
        settings.defaultArtist = defaultArtistToSave
        settings.orientation = orientationToSave
        settings.listenToMusicVariant = listenToMusicVariantToSave
        settings.scrollSpeed = scrollSpeedToSave
        settingsViewModel.restartApp()
    }

    val labelFontScale = settings.getSpecificFontScale(ScalePow.LABEL)
    val fontSizeLabelDp = dimensionResource(id = R.dimen.text_size_20) * labelFontScale
    val fontSizeLabelSp = with(LocalDensity.current) {
        fontSizeLabelDp.toSp()
    }

    val buttonFontScale = settings.getSpecificFontScale(ScalePow.BUTTON)
    val fontSizeButtonDp = dimensionResource(id = R.dimen.text_size_20) * buttonFontScale
    val fontSizeButtonSp = with(LocalDensity.current) {
        fontSizeButtonDp.toSp()
    }

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
                CommonTopAppBar(title = stringResource(id = R.string.title_settings))
                SettingsBodyPortrait(
                    theme = theme,
                    settingsRepository = settings,
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
                CommonSideAppBar(title = stringResource(id = R.string.title_settings))
                SettingsBodyLandscape(
                    theme = theme,
                    settingsRepository = settings,
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














