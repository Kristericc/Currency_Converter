package com.example.currencyconverter.view.onboarding.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.view.onboarding.model.IntroSlide


class OnBoardingRecyclerViewAdapter(private val introSlides: List<IntroSlide>)
    : RecyclerView.Adapter<OnBoardingSlideViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingSlideViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.slide_page, parent, false)
        return OnBoardingSlideViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: OnBoardingSlideViewHolder, position: Int) {
        if (position < introSlides.size) {
            val slide = introSlides[position]

            if (slide.animation) {
                holder.animation.visibility = View.VISIBLE
            } else {
                holder.animation.visibility = View.INVISIBLE
                holder.img.visibility = View.VISIBLE
                holder.img.setImageDrawable(slide.img)
            }
            holder.description.text = slide.description
            holder.title.text = slide.title

        }
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }
}