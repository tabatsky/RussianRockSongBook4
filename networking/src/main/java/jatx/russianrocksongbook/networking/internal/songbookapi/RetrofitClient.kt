package jatx.russianrocksongbook.networking.internal.songbookapi

import retrofit2.Retrofit
import javax.inject.Inject

internal class RetrofitClient @Inject constructor(
    retrofit: Retrofit
) {
    val songBookAPI = retrofit.create(SongBookAPI::class.java)
}
