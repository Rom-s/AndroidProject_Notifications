package com.example.td2

import android.app.Application
import com.example.td2.network.Api

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Api.INSTANCE = Api(this)
    }
}