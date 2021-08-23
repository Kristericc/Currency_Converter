package com.example.currencyconverter.data

import com.example.currencyconverter.data.remote.CurrencyRemoteSource
import com.example.currencyconverter.presentation.ReferAFriendViewModelTest.Companion.base
import com.example.currencyconverter.presentation.ReferAFriendViewModelTest.Companion.currencyList
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CurrencyRepositoryTest {
    private lateinit var cut: CurrencyRepository

    @Mock
    lateinit var currencyRepositoryRemoteSource: CurrencyRemoteSource

    @Before
    fun setUp() {
        currencyRepositoryRemoteSource = Mockito.mock(CurrencyRemoteSource::class.java)
        cut = CurrencyRepository(
            currencyRepositoryRemoteSource
        )
    }

    @Test
    fun `Given base currency Then return single list with currency pairs`() {
        // Given
        val currencyList = currencyList

        given(currencyRepositoryRemoteSource.getCurrencyData(base)).willReturn(
            Single.just(
                currencyList
            )
        )
        // When
        val returnValue = cut.getAllCurrencies(base).test()

        // Then
        returnValue
            .assertValue(currencyList)
    }

}