package jatx.russianrocksongbook.cloudsongs.api.methods

import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy

fun initCloudSearch() {
    CloudViewModel.getStoredInstance()?.cloudSearch("", OrderBy.BY_ID_DESC)
}