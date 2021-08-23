package com.example.currencyconverter.util

import com.example.currencyconverter.data.model.Currency

fun convertMapListToCurrencyList(toList: List<Pair<String, Double>>): MutableList<Currency> {
    val currencyList: MutableList<Currency> = mutableListOf()
    for (pair in toList) {
        val currency: Currency = Currency(pair.first,pair.second)
        currencyList.add(currency)
    }

    return currencyList
}