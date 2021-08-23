package com.example.currencyconverter.data

import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.data.remote.CurrencyRemoteSource
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class CurrencyRepository @Inject constructor(
     private val currencyRemoteSource: CurrencyRemoteSource
) {

     fun getAllCurrencies(base: String): Single<MutableList<Currency>> =
          currencyRemoteSource.getCurrencyData(base)
}
