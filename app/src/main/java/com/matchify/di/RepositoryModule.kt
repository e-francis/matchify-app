package com.matchify.di

import com.matchify.data.api.MatchifyApi
import com.matchify.data.repository.UserRepository
import com.matchify.data.repository.UserRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        api: MatchifyApi,
        moshi: Moshi
    ): UserRepository {
        return UserRepositoryImpl(api, moshi)
    }
}