package com.example.currencyconverter.view.recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R

class CurrencyCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
    var currencyTitle: TextView = itemView.findViewById(R.id.currencyTitle)
    var currencyRate: TextView = itemView.findViewById(R.id.rate)
}