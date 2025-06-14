package com.example.moneytracker.di

import android.content.Context
import androidx.room.Room
import com.example.moneytracker.data.local.AppDatabase
import com.example.moneytracker.data.local.dao.TransactionDao
import com.example.moneytracker.data.local.dao.CategoryDao
import com.example.moneytracker.data.local.dao.BalanceDao
import com.example.moneytracker.data.repository.CategoryRepositoryImpl
import com.example.moneytracker.data.repository.TransactionRepositoryImpl
import com.example.moneytracker.data.repository.BalanceRepositoryImpl
import com.example.moneytracker.domain.repository.CategoryRepository
import com.example.moneytracker.domain.repository.TransactionRepository
import com.example.moneytracker.domain.repository.BalanceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "money_tracker_db"
            )
            .fallbackToDestructiveMigration()
            .build()
        }

        @Provides
        fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()
        
        @Provides
        fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

        @Provides
        fun provideBalanceDao(database: AppDatabase): BalanceDao = database.balanceDao()
    }
    
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBalanceRepository(
        balanceRepositoryImpl: BalanceRepositoryImpl
    ): BalanceRepository
}
