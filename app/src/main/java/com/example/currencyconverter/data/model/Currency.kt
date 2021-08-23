package com.example.currencyconverter.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyAPIResponse (
    @SerializedName("result") val result : String,
    @SerializedName("documentation") val documentation : String,
    @SerializedName("terms_of_use") val terms_of_use : String,
    @SerializedName("time_last_update_unix") val time_last_update_unix : Int,
    @SerializedName("time_last_update_utc") val time_last_update_utc : String,
    @SerializedName("time_next_update_unix") val time_next_update_unix : Int,
    @SerializedName("time_next_update_utc") val time_next_update_utc : String,
    @SerializedName("base_code") val base_code : String,
    @SerializedName("conversion_rates") val conversion_rates : Any
)

data class Currency (
    val name: String,
    var rate: Double
)
