package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.model.data.ARTIST_ADD_ARTIST
import jatx.russianrocksongbook.model.data.ARTIST_ADD_SONG
import jatx.russianrocksongbook.model.data.ARTIST_CLOUD_SONGS
import jatx.russianrocksongbook.model.data.ARTIST_DONATION
import jatx.russianrocksongbook.model.preferences.*
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun SettingsScreen(mvvmViewModel: MvvmViewModel = viewModel()) {
    val theme = mvvmViewModel.settings.theme
    val settings = mvvmViewModel.settings

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
        mvvmViewModel.onRestartApp()
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
                    settings = settings,
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
                    settings = settings,
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

@Composable
private fun SettingsBodyPortrait(
    theme: Theme,
    settings: Settings,
    fontSizeLabelSp: TextUnit,
    fontSizeButtonSp: TextUnit,
    onThemePositionChanged: (Int) -> Unit,
    onFontScalePositionChanged: (Int) -> Unit,
    onDefaultArtistValueChanged: (String) -> Unit,
    onOrientationPositionChanged: (Int) -> Unit,
    onListenToMusicVariantPositionChanged: (Int) -> Unit,
    onScrollSpeedValueChanged: (Float) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(4.dp)) {
        ThemeRow(
            theme = theme,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            fontSize = fontSizeLabelSp,
            onPositionChanged = onThemePositionChanged
        )
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        FontScaleRow(
            theme = theme,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            fontSize = fontSizeLabelSp,
            onPositionChanged = onFontScalePositionChanged
        )
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        DefaultArtistRow(
            theme = theme,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            fontSize = fontSizeLabelSp,
            onValueChanged = onDefaultArtistValueChanged
        )
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        OrientationRow(
            theme = theme,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            fontSize = fontSizeLabelSp,
            onPositionChanged = onOrientationPositionChanged
        )
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        ListenToMusicVariantRow(
            theme = theme,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            fontSize = fontSizeLabelSp,
            onPositionChanged = onListenToMusicVariantPositionChanged
        )
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        ScrollSpeedRow(
            theme = theme,
            settings = settings,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            fontSize = fontSizeLabelSp,
            onValueChanged = onScrollSpeedValueChanged
        )
        Divider(
            color = theme.colorBg,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            colors = ButtonDefaults
                .buttonColors(
                    backgroundColor = theme.colorCommon,
                    contentColor = theme.colorMain
                ),
            onClick = onSaveClick
        ) {
            Text(
                text = stringResource(id = R.string.save_and_restart),
                textAlign = TextAlign.Center,
                fontSize = fontSizeButtonSp
            )
        }
    }
}

@Composable
private fun SettingsBodyLandscape(
    theme: Theme,
    settings: Settings,
    fontSizeLabelSp: TextUnit,
    fontSizeButtonSp: TextUnit,
    onThemePositionChanged: (Int) -> Unit,
    onFontScalePositionChanged: (Int) -> Unit,
    onDefaultArtistValueChanged: (String) -> Unit,
    onOrientationPositionChanged: (Int) -> Unit,
    onListenToMusicVariantPositionChanged: (Int) -> Unit,
    onScrollSpeedValueChanged: (Float) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeRow(
                theme = theme,
                settings = settings,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onThemePositionChanged
            )
            FontScaleRow(
                theme = theme,
                settings = settings,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onFontScalePositionChanged
            )
        }
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultArtistRow(
                theme = theme,
                settings = settings,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onValueChanged = onDefaultArtistValueChanged
            )
            OrientationRow(
                theme = theme,
                settings = settings,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onOrientationPositionChanged
            )
        }
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ListenToMusicVariantRow(
                theme = theme,
                settings = settings,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onListenToMusicVariantPositionChanged
            )
            ScrollSpeedRow(
                theme = theme,
                settings = settings,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onValueChanged = onScrollSpeedValueChanged
            )
        }
        Divider(
            color = theme.colorBg,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            colors = ButtonDefaults
                .buttonColors(
                    backgroundColor = theme.colorCommon,
                    contentColor = theme.colorMain
                ),
            onClick = onSaveClick
        ) {
            Text(
                text = stringResource(id = R.string.save_and_restart),
                textAlign = TextAlign.Center,
                fontSize = fontSizeButtonSp
            )
        }
    }
}


@Composable
private fun ThemeRow(
    modifier: Modifier,
    theme: Theme,
    settings: Settings,
    fontSize: TextUnit,
    onPositionChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.theme),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.theme_list)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = settings.theme.ordinal,
            onPositionChanged = onPositionChanged
        )

    }
}

@Composable
private fun FontScaleRow(
    modifier: Modifier,
    theme: Theme,
    settings: Settings,
    fontSize: TextUnit,
    onPositionChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.text_scale),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.font_scale_list)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = settings.commonFontScaleEnum.ordinal,
            onPositionChanged = onPositionChanged
        )

    }
}

@Composable
private fun DefaultArtistRow(
    mvvmViewModel: MvvmViewModel = viewModel(),
    modifier: Modifier,
    theme: Theme,
    settings: Settings,
    fontSize: TextUnit,
    onValueChanged: (String) -> Unit
) {
    val artistList by mvvmViewModel.artistList.collectAsState()
    val artists = ArrayList(artistList).apply {
        remove(ARTIST_CLOUD_SONGS)
        remove(ARTIST_ADD_SONG)
        remove(ARTIST_ADD_ARTIST)
        remove(ARTIST_DONATION)
    }
    val initialPosition = artists.indexOf(settings.defaultArtist)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.def_artist),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = artists.toTypedArray()
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = initialPosition,
            onPositionChanged = {
                onValueChanged(artists[it])
            }
        )

    }
}

@Composable
private fun OrientationRow(
    modifier: Modifier,
    theme: Theme,
    settings: Settings,
    fontSize: TextUnit,
    onPositionChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.orient_fix),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.orientation_list)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = settings.orientation.ordinal,
            onPositionChanged = onPositionChanged
        )

    }
}

@Composable
private fun ListenToMusicVariantRow(
    modifier: Modifier,
    theme: Theme,
    settings: Settings,
    fontSize: TextUnit,
    onPositionChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.listen_to_music),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.listen_to_music_variants)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = settings.listenToMusicVariant.ordinal,
            onPositionChanged = onPositionChanged
        )
    }
}

@Composable
private fun ScrollSpeedRow(
    modifier: Modifier,
    theme: Theme,
    settings: Settings,
    fontSize: TextUnit,
    onValueChanged: (Float) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var text by remember { mutableStateOf(settings.scrollSpeed.toString()) }
        Text(
            text = stringResource(id = R.string.scroll_speed),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        TextField(
            value = text,
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontSize = fontSize
            ),
            keyboardOptions = KeyboardOptions
                .Default
                .copy(keyboardType = KeyboardType.Number),
            onValueChange = {
                try {
                    text = if (it.isNotEmpty()) {
                        onValueChanged(it.toFloat())
                        it
                    } else {
                        onValueChanged(0f)
                        ""
                    }
                } catch (e: NumberFormatException) { }
            }
        )

    }
}

