package com.example.currencyconverter.view.onboarding.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.currencyconverter.R

class OnBoardingSlideViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var img: ImageView = itemView.findViewById(R.id.imageSlide)
    var animation: LottieAnimationView = itemView.findViewById(R.id.imageSlideIconAnimation)
    var title: TextView = itemView.findViewById(R.id.textTitle)
    var description: TextView = itemView.findViewById(R.id.textDescription)
}