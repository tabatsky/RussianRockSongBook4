package jatx.russianrocksongbook.database.internal.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jatx.russianrocksongbook.database.internal.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DbModule {
    @Singleton
    @Provides
    fun provideSongDao(@ApplicationContext context: Context) = AppDatabase.invoke(context).songDao()
}