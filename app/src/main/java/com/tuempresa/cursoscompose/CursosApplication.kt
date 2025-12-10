package com.tuempresa.cursoscompose

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

/**
 * Punto de entrada principal de la aplicación.
 * Su declaración en el AndroidManifest.xml es fundamental para que la app se inicie.
 */
class CursosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}
