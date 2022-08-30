package xyz.v.bhakt.dagger

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import xyz.v.bhakt.viewmodel.NetworkViewModel
import javax.inject.Singleton

@Module
class MediaplayerModule {
    @Singleton
    @Provides
    fun providemediaPlayer():MediaPlayer = MediaPlayer().also {
        it.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
    }
}