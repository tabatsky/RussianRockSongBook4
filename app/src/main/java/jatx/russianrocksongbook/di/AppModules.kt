package jatx.russianrocksongbook.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jatx.russianrocksongbook.api.BASE_URL
import jatx.russianrocksongbook.db.AppDatabase
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Provides
    fun provideSongDao(@ApplicationContext context: Context) = AppDatabase.invoke(context).songDao()
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()!!
}
