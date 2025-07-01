package com.example.moneytracker.di

import com.example.moneytracker.data.remote.GeminiAiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideGeminiAiService(): GeminiAiService {
        return GeminiAiService()
    }
}
