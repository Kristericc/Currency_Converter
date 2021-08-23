package com.example.currencyconverter.domain

import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.presentation.ReferAFriendViewModelTest.Companion.base
import com.example.currencyconverter.presentation.ReferAFriendViewModelTest.Companion.currencyList
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetNotificationSelectionIndexUseCaseTest {
    private lateinit var cut: GetCurrencyUseCase

    @Mock
    lateinit var currencyRepository: CurrencyRepository

    @Before
    fun setUp() {
        cut = GetCurrencyUseCase(
            currencyRepository = currencyRepository
        )
    }

    @Test
    fun `Given currencyList When execute Then return currencyList`() {
        // Given
        val currencyList = currencyList

        given(currencyRepository.getAllCurrencies(base)).willReturn(
            Single.just(
                currencyList
            )
        )
        // When
        val executeObserver = cut.execute(base).test()

        // Then
        executeObserver.assertValue(currencyList)
    }
}