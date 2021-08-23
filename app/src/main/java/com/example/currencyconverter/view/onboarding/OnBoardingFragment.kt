package com.example.currencyconverter.view.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.view.onboarding.model.IntroSlide
import com.example.currencyconverter.view.onboarding.recyclerview.OnBoardingRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_onboarding.*
import javax.inject.Inject

const val IS_FIRST_TIME = "is first time"

@AndroidEntryPoint
class OnBoardingFragment(private val activity: MainActivity) : Fragment() {

    companion object {
        fun newInstance(activity: MainActivity) =
            OnBoardingFragment(activity = activity)
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    private lateinit var introSliderAdapter: OnBoardingRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Currency Converter"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        viewPager.adapter = introSliderAdapter
        indicator.setViewPager(viewPager)
        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == introSliderAdapter.itemCount - 1) {
                        val animation = AnimationUtils.loadAnimation(
                            requireActivity(),
                            R.anim.animation_button_appearance
                        )
                        buttonNext.animation = animation
                        buttonNext.text = "Finish"
                        buttonNext.setOnClickListener {
                            sharedPreferences.edit().apply {
                                putBoolean(IS_FIRST_TIME, true)
                                apply()
                            }
                           activity.navigateToMainFragment()
                        }
                    } else {
                        buttonNext.text = "Next"
                        buttonNext.setOnClickListener {
                            viewPager.currentItem?.let {
                                viewPager.setCurrentItem(it + 1, false)
                            }
                        }
                    }
                }
            })
    }

    private fun setAdapter() {
        introSliderAdapter = OnBoardingRecyclerViewAdapter(
            listOf(
                IntroSlide(
                    title ="Welcome To Currency Converter",
                    description = "Your everyday currency converter in Your pocket",
                    img = null,
                    animation = true
                ),
                IntroSlide(
                    title = "Change Primary Currency",
                    description = "After choosing new primary currency, the app will load latest currency rates",
                    img = resources.getDrawable(R.drawable.new_currency),
                    animation = false
                ),
                IntroSlide(
                    title = "Change Secondary Currency",
                    description = "After clicking on secondary currency, a list of all available currencies will appear",
                    img = resources.getDrawable(R.drawable.change_currency),
                    animation = false

                )
            )
        )
    }
}