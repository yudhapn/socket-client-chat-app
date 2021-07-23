package id.ypran.chatapp

import android.app.Application
import id.ypran.chatapp.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appComponent)
        }
    }
}