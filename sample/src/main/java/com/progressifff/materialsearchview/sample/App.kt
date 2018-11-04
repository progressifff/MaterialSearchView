package com.progressifff.materialsearchview.sample

import android.app.Application

class App : Application() {

    companion object {
        private lateinit var instance: App
        fun get(): App {return instance}
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}