package com.example.currencyconverter.scheduler

import com.example.currencyconverter.domain.BaseSchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestSchedulerProvider : BaseSchedulerProvider() {

    override fun io(): Scheduler = Schedulers.trampoline()
    override fun ui(): Scheduler = Schedulers.trampoline()
    override fun computation(): Scheduler = Schedulers.trampoline()
    override fun newThread(): Scheduler = Schedulers.trampoline()
}