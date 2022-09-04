package xyz.v.bhakt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.v.bhakt.models.Song

class MediaViewModel:ViewModel() {
    val mediaPlayerState: MutableLiveData<Song> = MutableLiveData()
    val songIndexState: MutableLiveData<Int> = MutableLiveData()


    fun updateSong(song: Song, index: Int) {
        if (song.url.isNotBlank()) {
            mediaPlayerState.value = song
        }
        songIndexState.value = index
    }

}