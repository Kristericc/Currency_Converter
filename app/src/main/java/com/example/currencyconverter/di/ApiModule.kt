package com.example.currencyconverter.di

import com.example.currencyconverter.domain.ApiService
import com.example.currencyconverter.domain.ApiServiceProvider
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @JvmStatic
    @Reusable
    internal fun provideApiServiceProvider(retrofit: Retrofit): ApiServiceProvider =
        object : ApiServiceProvider {
            private val apiService by lazy { retrofit.create(ApiService::class.java) }

            override fun getInstance() = apiService
        }
}
