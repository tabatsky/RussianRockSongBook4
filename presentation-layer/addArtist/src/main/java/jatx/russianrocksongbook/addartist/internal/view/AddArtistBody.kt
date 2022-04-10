package jatx.russianrocksongbook.addartist.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.addartist.R
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow

@Composable
internal fun AddArtistBody(
) {
    val addArtistViewModel: AddArtistViewModel = viewModel()

    val theme = addArtistViewModel.settings.theme

    val fontScale = addArtistViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.colorBg)
            .padding(4.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1.0f)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.add_artist_manual),
                    color = theme.colorMain,
                    fontSize = fontSizeTextSp
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults
                .buttonColors(
                    backgroundColor = theme.colorCommon,
                    contentColor = theme.colorMain
                ),
            onClick = {
                addArtistViewModel.addSongsFromDir()
            }) {
            Text(text = stringResource(id = R.string.choose))
        }
    }
}