package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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

@Composable
fun SettingsScreenImplContent(
    artistList: List<String>,
    valueTheme: MutableState<Theme>,
    spinnerStateTheme: MutableState<SpinnerState>,
    valueFontScale: MutableState<FontScale>,
    spinnerStateFontScale: MutableState<SpinnerState>,
    valueDefaultArtist: MutableState<String>,
    spinnerStateDefaultArtist: MutableState<SpinnerState>,
    valueOrientation: MutableState<Orientation>,
    spinnerStateOrientation: MutableState<SpinnerState>,
    valueListenToMusicVariant: MutableState<ListenToMusicVariant>,
    spinnerStateListenToMusicVariant: MutableState<SpinnerState>,
    stringScrollSpeed: MutableState<String>,
    valueScrollSpeed: MutableFloatState,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    var themeToSave by valueTheme
    val onThemePositionChanged: (Int) -> Unit = {
        themeToSave = Theme.entries[it]
    }

    var fontScaleToSave by valueFontScale
    val onFontScalePositionChanged: (Int) -> Unit = {
        fontScaleToSave = FontScale.entries[it]
    }

    var defaultArtistToSave by valueDefaultArtist
    val onDefaultArtistValueChanged: (String) -> Unit = {
        defaultArtistToSave = it
    }

    var orientationToSave by valueOrientation
    val onOrientationPositionChanged: (Int) -> Unit = {
        orientationToSave = Orientation.entries[it]
    }

    var listenToMusicVariantToSave by valueListenToMusicVariant
    val onListenToMusicVariantPositionChanged: (Int) -> Unit = {
        listenToMusicVariantToSave =
            ListenToMusicVariant.entries[it]
    }

    var scrollSpeedToSave by valueScrollSpeed
    val onScrollSpeedValueChanged: (Float) -> Unit = {
        scrollSpeedToSave = it
    }

    val onSaveClick: () -> Unit = {
        submitAction(
            SaveSettings(
                themeToSave,
                fontScaleToSave.scale,
                defaultArtistToSave,
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
                    artistList = artistList,
                    valueTheme = valueTheme,
                    spinnerStateTheme = spinnerStateTheme,
                    valueFontScale = valueFontScale,
                    spinnerStateFontScale = spinnerStateFontScale,
                    valueDefaultArtist = valueDefaultArtist,
                    spinnerStateDefaultArtist = spinnerStateDefaultArtist,
                    valueOrientation = valueOrientation,
                    spinnerStateOrientation = spinnerStateOrientation,
                    valueListenToMusicVariant = valueListenToMusicVariant,
                    spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant,
                    stringScrollSpeed = stringScrollSpeed,
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
                    artistList = artistList,
                    valueTheme = valueTheme,
                    spinnerStateTheme = spinnerStateTheme,
                    valueFontScale = valueFontScale,
                    spinnerStateFontScale = spinnerStateFontScale,
                    valueDefaultArtist = valueDefaultArtist,
                    spinnerStateDefaultArtist = spinnerStateDefaultArtist,
                    valueOrientation = valueOrientation,
                    spinnerStateOrientation = spinnerStateOrientation,
                    valueListenToMusicVariant = valueListenToMusicVariant,
                    spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant,
                    stringScrollSpeed = stringScrollSpeed,
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