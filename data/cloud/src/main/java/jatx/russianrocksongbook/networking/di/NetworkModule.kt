package jatx.russianrocksongbook.networking.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jatx.russianrocksongbook.networking.songbookapi.BASE_URL
import jatx.synchronouscalladapterfactory.SynchronousCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}