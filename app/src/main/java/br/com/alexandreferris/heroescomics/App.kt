package br.com.alexandreferris.heroescomics

import android.app.Application
import br.com.alexandreferris.heroescomics.di.AppComponent
import br.com.alexandreferris.heroescomics.di.AppModule
import br.com.alexandreferris.heroescomics.di.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric

class App: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        // AdMob
        MobileAds.initialize(this, "ADMOB_API_KEY")

        // Fabric.IO
        Fabric.with(this, Crashlytics())
        appComponent = initDagger(this)
    }

    private fun initDagger(app: App): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()
}