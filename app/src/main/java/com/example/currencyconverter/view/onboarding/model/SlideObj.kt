package com.example.currencyconverter.view.onboarding.model

import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable

data class IntroSlide(
    val title: String,
    val description: String,
    val img: Drawable?,
    val animation: Boolean
)