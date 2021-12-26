package jatx.russianrocksongbook.cloudsongs.internal.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED

private const val PAGE_SIZE = 15

internal val CONFIG = PagingConfig(
    pageSize = PAGE_SIZE,
    maxSize = MAX_SIZE_UNBOUNDED
)