package com.example.currencyconverter.data.remote

import android.util.Log
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.domain.ApiServiceProvider
import com.example.currencyconverter.util.convertMapListToCurrencyList
import com.example.currencyconverter.view.MainFragment.Companion.timeStamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Reusable
import io.reactivex.Single
import java.lang.reflect.Type
import javax.inject.Inject

interface CurrencyRemoteSourceType {
    fun getCurrencyData(base: String): Single<MutableList<Currency>>
}

@Reusable
class CurrencyRemoteSource @Inject constructor(
    private val apiServiceProvider: ApiServiceProvider
): CurrencyRemoteSourceType {

    private val apiService by lazy { apiServiceProvider.getInstance() }

    override fun getCurrencyData(base: String): Single<MutableList<Currency>> {
        try {
            return apiService.getCurrencies(base).map { result ->
                timeStamp = result.time_last_update_utc
                val resultString = result.conversion_rates.toString()
                val type: Type =
                    object : TypeToken<Map<String?, Double?>?>() {}.type
                val currencyList: Map<String, Double> = Gson().fromJson(resultString, type)

                convertMapListToCurrencyList(currencyList.toList())
            }
        } catch (throwable: Throwable) {
            Log.d("REMOTE_SOURCE", throwable.toString())
            return Single.just(mutableListOf())
        }
    }

}
