package com.example.currencyconverter.presentation

import android.content.SharedPreferences
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.domain.GetCurrencyUseCase
import com.example.currencyconverter.scheduler.TestSchedulerProvider
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ReferAFriendViewModelTest {
    private lateinit var cut: MainViewModel

    @Mock
    lateinit var getCurrencyUseCase: GetCurrencyUseCase

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        cut = MainViewModel(
            getCurrencyUseCase = getCurrencyUseCase,
            schedulerProvider = TestSchedulerProvider(),
            sharedPreferences = sharedPreferences
        )
    }

    @Test
    fun `Given triggering loadCurrencies and successfully loads CurrencyList`() {
        // Given
        val expectedResult = currencyList

        given(getCurrencyUseCase.execute(base)).willReturn(
            Single.just(
                currencyList
            )
        )

        val setCurrencyRatesObserver = cut.setCurrencyRates().test()
        val setDefaultCurrencyObserver = cut.setDefaultCurrency().test()

        // When
        cut.loadCurrencies(base,false)

        // Then
        setCurrencyRatesObserver.assertValue(expectedResult)
        setDefaultCurrencyObserver.assertValue(expectedResult[0])

    }

    @Test
    fun `Given triggering loadCurrencies and unsuccessfully loads CurrencyList`() {
        // Given
        val expectedResult = Unit

        given(getCurrencyUseCase.execute(base)).willReturn(
            Single.just(
                mutableListOf()
            )
        )

        val setNoDataReceivedObserver = cut.noDataReceived().test()

        // When
        cut.loadCurrencies(base,true)

        // Then
        setNoDataReceivedObserver.assertValue(expectedResult)
    }

    @Test
    fun `Given triggering getCalculatedAmount with valid params`() {
        // Given
        val expectedResult = 2.9136.toString()

        val setSetCalculatedRateObserver = cut.setCalculatedAmount().test()

        // When
        cut.getCalculatedAmount(2.163,1.347)

        // Then
        setSetCalculatedRateObserver.assertValue(expectedResult)
    }

    @Test
    fun `Given triggering getUpdatedRate and successfully finds USD`() {
        // Given
        val expectedResult = currencyList[2].rate
        val setSetUpdatedRate = cut.setUpdatedRate().test()

        // When
        cut.getUpdatedRate(currencyList,"UFC")

        // Then
        setSetUpdatedRate.assertValue(expectedResult)
    }

    companion object {
        val currencyList: MutableList<Currency> = mutableListOf(
            Currency("AFG",1.1),
            Currency("USD",0.81),
            Currency("UFC",0.14)
        )

        const val base = "EUR"
    }

}
