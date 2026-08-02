package com.bkushagra742.ascend.di

import android.content.Context
import androidx.room.Room
import com.bkushagra742.ascend.data.local.AscendDatabase
import com.bkushagra742.ascend.data.local.dao.HabitDao
import com.bkushagra742.ascend.data.local.dao.InventoryDao
import com.bkushagra742.ascend.data.local.dao.PlayerProfileDao
import com.bkushagra742.ascend.data.local.dao.QuestDao
import com.bkushagra742.ascend.data.local.dao.StreakDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AscendDatabase =
        Room.databaseBuilder(context, AscendDatabase::class.java, AscendDatabase.DATABASE_NAME)
            // No fallbackToDestructiveMigration() — see AscendDatabase kdoc. Missing
            // migrations should crash in debug (loud failure) rather than silently
            // wipe a user's progress.
            .build()

    @Provides
    fun providePlayerProfileDao(db: AscendDatabase): PlayerProfileDao = db.playerProfileDao()

    @Provides
    fun provideQuestDao(db: AscendDatabase): QuestDao = db.questDao()

    @Provides
    fun provideHabitDao(db: AscendDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideStreakDao(db: AscendDatabase): StreakDao = db.streakDao()

    @Provides
    fun provideInventoryDao(db: AscendDatabase): InventoryDao = db.inventoryDao()
}
