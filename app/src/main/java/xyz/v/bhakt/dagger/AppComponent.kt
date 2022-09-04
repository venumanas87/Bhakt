package xyz.v.bhakt.dagger

import dagger.Component
import xyz.v.bhakt.MainActivity
import xyz.v.bhakt.SongListActivity
import xyz.v.bhakt.fragments.HomeFragment
import xyz.v.bhakt.utils.MySp
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,MediaplayerModule::class])
interface AppComponent{

    fun inject(target:SongListActivity)
    fun inject(target:MainActivity)
    fun inject(target:HomeFragment)

}