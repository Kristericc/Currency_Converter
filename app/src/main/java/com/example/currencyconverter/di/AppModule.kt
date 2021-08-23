package com.example.currencyconverter.di

import android.content.Context
import com.example.currencyconverter.CustomApplication
import com.example.currencyconverter.domain.AppSchedulerProvider
import com.example.currencyconverter.domain.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): CustomApplication {
        return app as CustomApplication
    }

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()
}
