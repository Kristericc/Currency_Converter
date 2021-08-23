package com.example.currencyconverter.view

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.data.model.Currency
import com.example.currencyconverter.presentation.CURRENCY_LIST
import com.example.currencyconverter.presentation.MainViewModel
import com.example.currencyconverter.presentation.TIME_STAMP
import com.example.currencyconverter.util.isOnline
import com.example.currencyconverter.util.roundDouble
import com.example.currencyconverter.view.recyclerview.CurrencyRecyclerViewAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import javax.inject.Inject

const val CURRENT_CURRENCY = "current currency"
const val SELECTED_CURRENCY = "selected currency"

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        var timeStamp: String = ""
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: CurrencyRecyclerViewAdapter
    private lateinit var defaultColor: ColorStateList
    private lateinit var currentCurrency: String

    private var isFirstInpFocused: Boolean = false
    private var isFirstCurrencyFocused: Boolean = false
    private var isRecyclerViewSetUp: Boolean = false
    private var isViewUp: Boolean = false
    private var triedToUpdateCurrency: Boolean = false

    private var selectedCurrency: Currency? = null

    private var currencyList: MutableList<Currency> = mutableListOf()

    private var subscriptions = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        setOutputListeners()
        setInputListeners()
        changeCurrencies()
        setOnLayoutClickListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Currency Converter"

        animateDown()

        getDataFromSharedPrefs()

        Handler(Looper.getMainLooper()).postDelayed({
            updateEverySecond()
        }, 2000)
    }

    private fun setOutputListeners() {
        viewModel.outputs.setCurrencyRates()
            .subscribe { cList ->
                if (!isRecyclerViewSetUp) {
                    setUpRecyclerView(cList)
                    if (selectedCurrency != null) {
                        setCurrency()
                    }
                } else {
                    updateRecyclerView(cList)
                    if(triedToUpdateCurrency) {
                        viewModel.inputs.getUpdatedRate(cList, selectedCurrency!!.name)
                    }
                }
                triedToUpdateCurrency = false
            }.addTo(subscriptions)

        viewModel.outputs.setDefaultCurrency()
            .subscribe { currency ->
                selectedCurrency = currency
                setCurrency()
            }.addTo(subscriptions)

        viewModel.outputs.setCalculatedAmount()
            .subscribe { amount ->
                setAmount(amount)
            }.addTo(subscriptions)

        viewModel.outputs.setUpdatedRate()
            .subscribe { rate ->
                selectedCurrency?.rate = rate
                setCurrency()
            }.addTo(subscriptions)

        viewModel.outputs.noDataReceived()
            .subscribe {
                if (currencyList.isNotEmpty()) {
                    if (!triedToUpdateCurrency) {
                        showMainLayout()
                    } else {
                        offlineWhenChangingCurrency()
                        triedToUpdateCurrency = false
                    }
                } else {
                    offlineMode()
                }
            }.addTo(subscriptions)
    }

    private fun setInputListeners() {
        firstInput.addTextChangedListener {
            if (!firstInput.text.toString().isNullOrEmpty()) {
                isFirstInpFocused = true
                viewModel.getCalculatedAmount(
                    firstInput.text.toString().toDouble(),
                    selectedCurrency!!.rate
                )
            }
        }

        secondInput.addTextChangedListener {
            if (!secondInput.text.toString().isNullOrEmpty()) {
                isFirstInpFocused = false
                viewModel.getCalculatedAmount(
                    secondInput.text.toString().toDouble(),
                    1 / selectedCurrency!!.rate
                )
            }
        }
    }

    private fun setUpRecyclerView(cList: MutableList<Currency>) {
        currencyList = cList
        isRecyclerViewSetUp = true

        currencyRecyclerView.setHasFixedSize(true)
        currencyRecyclerView.layoutManager =
            GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        adapter = CurrencyRecyclerViewAdapter(currencyList, this, currentCurrency)
        currencyRecyclerView.adapter = adapter

        showMainLayout()
    }

    private fun updateRecyclerView(cList: MutableList<Currency>) {
        var hasChanges: Boolean = false
        for (i in cList.indices) {
            if (currencyList[i] != cList[i]) {
                hasChanges = true
            }
        }

        if (hasChanges) {
            adapter.updateAdapter(cList, currentCurrency)
        }

        showMainLayout()
    }

    private fun setCurrency() {
        firstCurName.text = currentCurrency
        secondCurrName.text = selectedCurrency?.name

        firstCurrRate.text =
            "1 $currentCurrency = ${roundDouble(selectedCurrency!!.rate)} ${selectedCurrency?.name}"
        secondCurrRate.text =
            "1 ${selectedCurrency?.name} = ${roundDouble(1 / selectedCurrency!!.rate)} $currentCurrency"
    }

    private fun setAmount(amount: String) {
        if (isFirstInpFocused) {
            firstCurrRate.text =
                "${firstInput.text} $currentCurrency = $amount ${selectedCurrency?.name}"
        } else {
            secondCurrRate.text =
                "${secondInput.text} ${selectedCurrency?.name} = $amount $currentCurrency"
        }
    }

    private fun updateEverySecond() {
            Handler(Looper.getMainLooper()).postDelayed({
                if (isOnline(requireContext())) {
                    GlobalScope.launch(Dispatchers.IO) {
                        viewModel.inputs.loadCurrencies(currentCurrency, selectedCurrency != null)
                    }
                    hideOffline()
                } else {
                    offlineMode()
                }
                updateEverySecond()
            }, 1000)
    }

    private fun changeCurrencies() {
        defaultColor = firstCurName.textColors

        firstCurName.setOnClickListener {
            firstCurName.setTextColor(resources.getColor(R.color.color_warning))
            secondCurrName.setTextColor(defaultColor)
            if (!isViewUp) {
                isViewUp = true
                animateUp()
            }
            isFirstCurrencyFocused = true
            hideKeyBoard()
        }

        secondCurrName.setOnClickListener {
            secondCurrName.setTextColor(resources.getColor(R.color.color_warning))
            firstCurName.setTextColor(defaultColor)
            if (!isViewUp) {
                isViewUp = true
                animateUp()
            }
            isFirstCurrencyFocused = false
            hideKeyBoard()
        }
    }

    fun onCurrencyClicked(currency: Currency) {
        isViewUp = false
        animateDown()
        if (isFirstCurrencyFocused) {
            firstCurName.setTextColor(defaultColor)
            hideMainLayout()
            if (isOnline(requireContext())) {
                triedToUpdateCurrency = true

                currentCurrency = currency.name

                hideOffline()
                 GlobalScope.launch(Dispatchers.IO) {
                     delay(3000)
                     viewModel.inputs.loadCurrencies(currentCurrency, selectedCurrency != null)
                 }
            } else {
                offlineWhenChangingCurrency()
            }
        } else {
            secondCurrName.setTextColor(defaultColor)
            selectedCurrency = currency
            setCurrency()
        }

        setInputsToDefault()
    }

    private fun animateUp() {
        nestedScrollView.translationY = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        nestedScrollView.animate()
            .translationY(0f)
            .setInterpolator(LinearInterpolator())
            .setDuration(1000)
            .start()
    }

    private fun animateDown() {
        val animator = ObjectAnimator.ofFloat(
            nestedScrollView,
            View.TRANSLATION_Y,
            Resources.getSystem().displayMetrics.heightPixels.toFloat()
        )
        animator.duration = 1000
        animator.start()
    }

    private fun hideMainLayout() {
        mainLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showMainLayout() {
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
    }

    private fun hideViews() {
        progressBar.visibility = View.GONE
        mainLayout.visibility = View.GONE
    }

    private fun hideOffline() {
        offline.visibility = View.GONE
        lastUpdated.visibility = View.GONE
        noDataToShow.visibility = View.GONE
    }

    private fun offlineMode() {
        if (currencyList.isNotEmpty()) {
            showMainLayout()
            offline.visibility = View.VISIBLE
            lastUpdated.visibility = View.VISIBLE
            lastUpdated.text = "Last Updated: $timeStamp"
            setCurrency()
            if (!isRecyclerViewSetUp) {
                setUpRecyclerView(currencyList)
            }
        } else {
            hideViews()
            noDataToShow.visibility = View.VISIBLE
        }
    }

    private fun offlineWhenChangingCurrency() {
        Toast.makeText(
            requireContext(),
            getString(R.string.cant_update_currency),
            Toast.LENGTH_LONG
        ).show()
        offlineMode()
    }

    private fun setOnLayoutClickListener() {
        constraintLayout.setOnClickListener {
            secondCurrName.setTextColor(defaultColor)
            firstCurName.setTextColor(defaultColor)

            animateDown()
            hideKeyBoard()

            isViewUp = false
        }
    }

    private fun setInputsToDefault() {
        firstInput.setText("1")
        secondInput.setText("1")
    }

    private fun getDataFromSharedPrefs() {
        if (sharedPreferences.getString(SELECTED_CURRENCY, null) != null) {
            selectedCurrency = Gson().fromJson(
                sharedPreferences.getString(SELECTED_CURRENCY, null),
                Currency::class.java
            )
        }
        if (sharedPreferences.getString(CURRENCY_LIST, null) != null) {
            val type: Type =
                object : TypeToken<MutableList<Currency>>() {}.type
            currencyList = Gson().fromJson(sharedPreferences.getString(CURRENCY_LIST, null), type)
        }
        currentCurrency = sharedPreferences.getString(CURRENT_CURRENCY, "EUR").toString()
        timeStamp = sharedPreferences.getString(TIME_STAMP, "").toString()
    }

    private fun hideKeyBoard() {
        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.edit()
            .putString(CURRENT_CURRENCY, currentCurrency)
            .putString(SELECTED_CURRENCY, Gson().toJson(selectedCurrency))
            .apply()
    }

}
