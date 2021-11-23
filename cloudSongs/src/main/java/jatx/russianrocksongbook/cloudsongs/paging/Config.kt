package jatx.russianrocksongbook.cloudsongs.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED

const val PAGE_SIZE = 15

val CONFIG = PagingConfig(
    pageSize = PAGE_SIZE,
    maxSize = MAX_SIZE_UNBOUNDED
)