package cl.duoc.tecexpress

import android.app.Application

class TecExpressApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
