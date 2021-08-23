package com.example.currencyconverter.view.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.util.roundDouble
import com.example.currencyconverter.view.MainFragment

class CurrencyRecyclerViewAdapter(
    private var currencyList: MutableList<Currency>,
    private val fragment: MainFragment,
    private var currentCurrency: String
) : RecyclerView.Adapter<CurrencyCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.currency_card, parent, false)
        return CurrencyCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: CurrencyCardViewHolder, position: Int) {
        if (position < currencyList.size) {
            val currency = currencyList[position]

            holder.currencyTitle.text = currency.name
            holder.currencyRate.text = "1 $currentCurrency = ${roundDouble(currency.rate)} ${currency.name}"

            holder.itemView.setOnClickListener {
                onCurrencyClicked(currency)
            }
        }
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    private fun onCurrencyClicked(currency: Currency) {
        fragment.onCurrencyClicked(currency)
    }

    fun updateAdapter(
        cList: MutableList<Currency>,
        currentCurrency: String
    ) {
        this.currentCurrency = currentCurrency
        this.currencyList.clear()
        this.currencyList.addAll(cList)
        this.notifyDataSetChanged()
    }
}