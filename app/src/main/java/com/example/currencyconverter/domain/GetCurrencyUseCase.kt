package com.example.currencyconverter.domain

import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.model.Currency
import io.reactivex.Single
import javax.inject.Inject

class GetCurrencyUseCase@Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    fun execute(base: String): Single<MutableList<Currency>> =
        currencyRepository.getAllCurrencies(base)

}
