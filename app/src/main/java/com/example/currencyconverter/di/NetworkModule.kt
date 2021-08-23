package com.example.currencyconverter.di

import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.domain.GetCurrencyUseCase
import com.example.currencyconverter.domain.ApiServiceProvider
import com.example.currencyconverter.data.remote.CurrencyRemoteSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesCurrencyServices(currencyRepository: CurrencyRepository): GetCurrencyUseCase {
        return GetCurrencyUseCase(
            currencyRepository
        )
    }

    @Singleton
    @Provides
    fun providesCurrencyRemoteSource(apiServiceProvider: ApiServiceProvider): CurrencyRemoteSource {
        return CurrencyRemoteSource(
            apiServiceProvider
        )
    }
}
