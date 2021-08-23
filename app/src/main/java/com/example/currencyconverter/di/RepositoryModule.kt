package com.example.currencyconverter.di

import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.remote.RequestInterceptor
import com.example.currencyconverter.data.remote.CurrencyRemoteSource
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesCurrencyRepository(currencyRemoteSource: CurrencyRemoteSource) : CurrencyRepository {
        return CurrencyRepository(
            currencyRemoteSource
        )
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/e4dacd1d33e0292f5f3c31bd/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(reqInterceptorOkHttpClient: RequestInterceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = (HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(reqInterceptorOkHttpClient)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideReqInterceptor(): RequestInterceptor {
        return RequestInterceptor()
    }

}
