package com.example.currencyconverter

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.currencyconverter.view.onboarding.IS_FIRST_TIME
import dagger.hilt.android.AndroidEntryPoint
import com.example.currencyconverter.view.MainFragment
import com.example.currencyconverter.view.onboarding.OnBoardingFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            sharedPreferences.apply {
                if (!getBoolean(IS_FIRST_TIME, false)) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, OnBoardingFragment.newInstance(this@MainActivity))
                        .commitNow()
                } else {
                    navigateToMainFragment()
                }
            }
        }
    }

    fun navigateToMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }
}