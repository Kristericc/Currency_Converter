package com.example.currencyconverter.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application) : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application.applicationContext);
    }
}