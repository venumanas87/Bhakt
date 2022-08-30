package xyz.v.bhakt.application

import android.app.Application
import xyz.v.bhakt.dagger.AppComponent
import xyz.v.bhakt.dagger.AppModule
import xyz.v.bhakt.dagger.DaggerAppComponent

class BhaktApplication: Application(){

    lateinit var bhaktComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        bhaktComponent = initDagger(this)
    }

    private fun initDagger(app: BhaktApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

}