package com.vaibhavdhunde.android.practice.realtimelocation.application

import android.app.Application
import com.vaibhavdhunde.android.practice.realtimelocation.BuildConfig
import com.vaibhavdhunde.android.practice.realtimelocation.remote.MyApi
import com.vaibhavdhunde.android.practice.realtimelocation.remote.NetworkInterceptor
import com.vaibhavdhunde.android.practice.realtimelocation.ui.explore.MyRemoteRepo
import com.vaibhavdhunde.android.practice.realtimelocation.util.MyViewModelFactory
import io.paperdb.Paper
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import timber.log.Timber

@Suppress("unused")
class MyApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))
        bind() from singleton { NetworkInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { MyRemoteRepo(instance()) }
        bind() from provider { MyViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initPaperDb()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun initPaperDb() {
        Paper.init(applicationContext)
    }
}