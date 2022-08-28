package xyz.v.bhakt

import android.media.MediaPlayer
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import xyz.v.bhakt.adapter.ListCardAdapter
import xyz.v.bhakt.databinding.ActivitySongListBinding
import xyz.v.bhakt.models.Song
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.NetworkViewModel

class SongListActivity : AppCompatActivity() {
    lateinit var backIv:ImageView
    lateinit var binding: ActivitySongListBinding
    lateinit var list:ArrayList<Song>
    lateinit var mediaPlayer: MediaPlayer
    lateinit var networkViewModel: NetworkViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        makeStatusBarTransparent()
        list = ArrayList()

        //binding.recycler.adapter = ListCardAdapter(list)
        binding.backBtn.setOnClickListener {
            finish()
        }

        networkViewModel.fetchGaneshSong()
        setObservers()
       /* mediaPlayer = MediaPlayer()

        mediaPlayer.setDataSource("https://pagalworld.com.se/siteuploads/files/sfd128/63903/Om%20Gan%20Ganpataye%20Namo%20Namah(PagalWorld.com.se).mp3")

        mediaPlayer.prepare()

        mediaPlayer.start()*/
    }


    private fun setObservers() {
        networkViewModel.ganeshSongLiveData.observe(this) { res ->
            when(res){
                is Resource.Success->{
                    res.data?.let {
                      list.clear()
                      for(s:Song in it){
                          list.add(s)
                      }
                      binding.recycler.adapter = ListCardAdapter(list)
                    }
                }
                is Resource.Error ->{
                    Snackbar.make(binding.root,"Error Loading Data", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{

                }
            }
        }
    }
}