package com.example.currencyconverter.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.domain.GetCurrencyUseCase
import com.example.currencyconverter.domain.SchedulerProvider
import com.example.currencyconverter.util.roundDouble
import com.example.currencyconverter.view.MainFragment.Companion.timeStamp
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

interface MainViewModelInputs {
    fun loadCurrencies(base: String, emptyCurrency: Boolean)
    fun getCalculatedAmount(amount: Double, rate: Double)
    fun getUpdatedRate(currencyList: MutableList<Currency>, selectedCurrency: String)
}

interface MainViewModelOutputs {
    fun setCurrencyRates(): Observable<MutableList<Currency>>
    fun setDefaultCurrency(): Observable<Currency>
    fun setCalculatedAmount(): Observable<String>
    fun setUpdatedRate(): Observable<Double>
    fun noDataReceived(): Observable<Unit>
}

const val CURRENCY_LIST = "currency list"
const val TIME_STAMP = "time stamp"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val schedulerProvider: SchedulerProvider,
    private val sharedPreferences: SharedPreferences
) : ViewModel(),
    MainViewModelInputs,
    MainViewModelOutputs {

    val inputs: MainViewModelInputs = this
    val outputs: MainViewModelOutputs = this

    private val subscriptions = CompositeDisposable()

    private val setCurrencyRates = PublishSubject.create<MutableList<Currency>>()
    private val setDefaultCurrency = PublishSubject.create<Currency>()
    private val setCalculatedAmount = PublishSubject.create<String>()
    private val setUpdatedRate = PublishSubject.create<Double>()
    private val noDataReceived = PublishSubject.create<Unit>()

    override fun loadCurrencies(base: String, emptyCurrency: Boolean) {
        getCurrencyUseCase.execute(base)
            .subscribe({ currencyList ->
                if (currencyList.isNotEmpty()) {
                    // removes the same currency as base currency
                    currencyList.removeAt(0)
                    setCurrencyRates.onNext(currencyList)
                    if (!emptyCurrency) {
                        setDefaultCurrency(currencyList)
                    }
                    addToSharedPrefs(currencyList)
                } else {
                    noDataReceived.onNext(Unit)
                }
            }, { throwable ->
                Log.d("VIEWMODEL", throwable.toString())
                noDataReceived.onNext(Unit)
            }
            ).addTo(subscriptions)

    }

    override fun getCalculatedAmount(amount: Double, rate: Double) {
        val calcAmount = roundDouble(amount * rate)
        setCalculatedAmount.onNext(calcAmount)
    }

    override fun getUpdatedRate(currencyList: MutableList<Currency>, selectedCurrency: String) {
        currencyList.forEach { currency ->
            if (currency.name == selectedCurrency) {
                setUpdatedRate.onNext(currency.rate)
            }
        }
    }

    private fun setDefaultCurrency(currencyList: MutableList<Currency>) {
        var found = false
        currencyList.forEach { currency ->
            if (currency.name == "USD") {
                found = true
                setDefaultCurrency.onNext(currency)
            }
        }

        if (!found) {
            setDefaultCurrency.onNext(currencyList.random())
        }
    }

    private fun addToSharedPrefs(currencyList: MutableList<Currency>) {
        sharedPreferences.edit()
            .putString(CURRENCY_LIST, Gson().toJson(currencyList))
            .putString(TIME_STAMP, timeStamp)
            .apply()
    }


    override fun setCurrencyRates(): Observable<MutableList<Currency>> =
        setCurrencyRates.observeOnUiAndHide()

    override fun setDefaultCurrency(): Observable<Currency> =
        setDefaultCurrency.observeOnUiAndHide()

    override fun setCalculatedAmount(): Observable<String> =
        setCalculatedAmount.observeOnUiAndHide()

    override fun setUpdatedRate(): Observable<Double> =
        setUpdatedRate.observeOnUiAndHide()

    override fun noDataReceived(): Observable<Unit> =
        noDataReceived.observeOnUiAndHide()

    private fun <T> Observable<T>.observeOnUiAndHide() =
        observeOn(schedulerProvider.ui()).hide()
}