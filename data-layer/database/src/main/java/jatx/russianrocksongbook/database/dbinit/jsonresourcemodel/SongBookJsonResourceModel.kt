package jatx.russianrocksongbook.database.dbinit.jsonresourcemodel

internal data class SongBookJsonResourceModel(
    val songbook: List<SongJsonResourceModel>
)

internal data class SongJsonResourceModel(
    val title: String,
    val text: String
)