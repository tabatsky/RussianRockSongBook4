package jatx.russianrocksongbook.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jatx.russianrocksongbook.data.FileSystemAdapter
import jatx.russianrocksongbook.api.BASE_URL
import jatx.russianrocksongbook.api.RetrofitClient
import jatx.russianrocksongbook.data.Settings
import jatx.russianrocksongbook.data.SongBookAPIAdapter
import jatx.russianrocksongbook.data.SongRepository
import jatx.russianrocksongbook.data.UserInfo
import jatx.russianrocksongbook.db.AppDatabase
import jatx.russianrocksongbook.db.dao.SongDao
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSongDao(@ApplicationContext context: Context) = AppDatabase.invoke(context).songDao()

    @Singleton
    @Provides
    fun provideSongRepo(songDao: SongDao) = SongRepository(songDao)

    @Singleton
    @Provides
    fun provideSettings(@ApplicationContext context: Context) = Settings(context)

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()!!

    @Singleton
    @Provides
    fun provideRetrofitClient(retrofit: Retrofit) = RetrofitClient(retrofit)

    @Singleton
    @Provides
    fun provideSongBookAPIAdapter(retrofitClient: RetrofitClient) =
        SongBookAPIAdapter(retrofitClient)

    @Singleton
    @Provides
    fun provideUserInfo(@ApplicationContext context: Context) =
        UserInfo(context)

    @Singleton
    @Provides
    fun provideFileSystemAdapter(
        @ApplicationContext context: Context,
        songRepo: SongRepository
    ) = FileSystemAdapter(context, songRepo)

    @Singleton
    @Provides
    fun provideViewModel(
        @ApplicationContext context: Context,
        songRepo: SongRepository,
        settings: Settings,
        songBookAPIAdapter: SongBookAPIAdapter,
        userInfo: UserInfo,
        fileSystemAdapter: FileSystemAdapter
    ) = MvvmViewModel(
        context,
        songRepo,
        settings,
        songBookAPIAdapter,
        userInfo,
        fileSystemAdapter
    )
}