package com.bkushagra742.ascend.di

import com.bkushagra742.ascend.data.repository.FocusRepositoryImpl
import com.bkushagra742.ascend.data.repository.HabitRepositoryImpl
import com.bkushagra742.ascend.data.repository.PlayerRepositoryImpl
import com.bkushagra742.ascend.data.repository.QuestRepositoryImpl
import com.bkushagra742.ascend.data.repository.StreakRepositoryImpl
import com.bkushagra742.ascend.domain.repository.FocusRepository
import com.bkushagra742.ascend.domain.repository.HabitRepository
import com.bkushagra742.ascend.domain.repository.PlayerRepository
import com.bkushagra742.ascend.domain.repository.QuestRepository
import com.bkushagra742.ascend.domain.repository.StreakRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Binds (not @Provides) — this is a pure interface-to-implementation mapping, so Binds
 * avoids the boilerplate of manually constructing the impl. Every future repository
 * follows this exact same two-line pattern.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindQuestRepository(impl: QuestRepositoryImpl): QuestRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindStreakRepository(impl: StreakRepositoryImpl): StreakRepository

    @Binds
    @Singleton
    abstract fun bindFocusRepository(impl: FocusRepositoryImpl): FocusRepository
}
