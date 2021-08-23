package com.example.currencyconverter.domain

import com.example.currencyconverter.data.remote.ServiceProvider
import com.example.currencyconverter.data.model.CurrencyAPIResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("latest/{base}")
    fun getCurrencies(
        @Path("base") base: String
    ): Single<CurrencyAPIResponse>

}

interface ApiServiceProvider : ServiceProvider<ApiService>
