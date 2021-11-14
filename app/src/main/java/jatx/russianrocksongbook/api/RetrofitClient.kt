package jatx.russianrocksongbook.api

import retrofit2.Retrofit
import javax.inject.Inject

class RetrofitClient @Inject constructor(
    private val retrofit: Retrofit
) {
    val songBookAPI: SongBookAPI
        get() = retrofit.create(SongBookAPI::class.java)
}
