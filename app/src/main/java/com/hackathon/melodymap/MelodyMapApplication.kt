package com.hackathon.melodymap

import android.app.Application

class MelodyMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConfigManager.load(this)
    }
}
