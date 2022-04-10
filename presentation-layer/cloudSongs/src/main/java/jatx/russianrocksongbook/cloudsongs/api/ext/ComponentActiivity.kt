package jatx.russianrocksongbook.cloudsongs.api.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy

fun ComponentActivity.initCloudSearch() {
    val cloudViewModel: CloudViewModel by viewModels()
    cloudViewModel.cloudSearch("", OrderBy.BY_ID_DESC)
}