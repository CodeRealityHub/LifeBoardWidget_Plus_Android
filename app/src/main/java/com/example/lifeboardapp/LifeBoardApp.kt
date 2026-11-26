package com.example.lifeboardapp


import android.app.Application
import com.example.lifeboardapp.di.AppContainer

class LifeBoardApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize the central dependency container
        container = AppContainer(this)
    }
}
