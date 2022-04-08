package jatx.russianrocksongbook.networking.songbookapi

import retrofit2.Retrofit
import javax.inject.Inject

class RetrofitClient @Inject constructor(
    retrofit: Retrofit
) {
    internal val songBookAPI: SongBookAPI = retrofit.create(SongBookAPI::class.java)
}
