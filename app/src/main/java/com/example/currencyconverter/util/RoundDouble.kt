package com.example.currencyconverter.util

import java.math.RoundingMode
import java.text.DecimalFormat

fun roundDouble(double: Double): String {
    val df = DecimalFormat("0.####")
    df.roundingMode = RoundingMode.HALF_EVEN

    return df.format(double).toBigDecimal().toPlainString()
}