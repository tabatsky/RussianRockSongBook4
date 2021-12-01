package jatx.russianrocksongbook.model.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jatx.russianrocksongbook.model.api.BASE_URL
import jatx.synchronouscalladapterfactory.SynchronousCallAdapterFactory
import jatx.russianrocksongbook.model.db.AppDatabase
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Singleton
    @Provides
    fun provideSongDao(@ApplicationContext context: Context) = AppDatabase.invoke(context).songDao()
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()!!
}
