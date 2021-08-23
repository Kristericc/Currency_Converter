package com.example.currencyconverter.domain

import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun newThread(): Scheduler

    fun <T> doOnIoObserveOnMainObservable(): ObservableTransformer<T, T>
}

abstract class BaseSchedulerProvider : SchedulerProvider {

    override fun <T> doOnIoObserveOnMainObservable(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(io())
                .unsubscribeOn(io())
                .observeOn(ui())
        }
    }
}

class AppSchedulerProvider : BaseSchedulerProvider() {

    override fun io(): Scheduler = Schedulers.io()
    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    override fun computation() = Schedulers.computation()
    override fun newThread() = Schedulers.newThread()
}