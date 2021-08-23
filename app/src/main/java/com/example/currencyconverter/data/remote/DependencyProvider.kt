package com.example.currencyconverter.data.remote

interface DependencyProvider<INSTANCE_TYPE> {
    fun getInstance(): INSTANCE_TYPE
}