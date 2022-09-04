package xyz.v.bhakt.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer


class MediaPlayerImpl(val mediaPlayer: MediaPlayer,context: Context) :AudioManager.OnAudioFocusChangeListener {

    var result: Int?

    init {
        val am: AudioManager? = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        result = am?.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


    }


    fun play(){
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {
            mediaPlayer.start()
            println("Venu playing  $result")
        }else{
            println("Venu playing failed $result")
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer.start()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer.pause()
            }
        }
    }

}