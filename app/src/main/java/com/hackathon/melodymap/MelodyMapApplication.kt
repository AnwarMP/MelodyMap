package com.hackathon.melodymap

import android.app.Application
//import android.util.Log
//import org.opencv.android.OpenCVLoader

class MelodyMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConfigManager.load(this)

    }
}
