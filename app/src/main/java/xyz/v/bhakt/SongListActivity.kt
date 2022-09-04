package xyz.v.bhakt

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.*
import xyz.v.bhakt.adapter.ListCardAdapter
import xyz.v.bhakt.application.BhaktApplication
import xyz.v.bhakt.databinding.ActivitySongListBinding
import xyz.v.bhakt.models.Song
import xyz.v.bhakt.utils.Constants.Companion.SONG
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.utils.MySp
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.MediaViewModel
import xyz.v.bhakt.viewmodel.NetworkViewModel
import java.lang.Runnable
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SongListActivity : AppCompatActivity() {
    lateinit var backIv:ImageView
    lateinit var binding: ActivitySongListBinding
    lateinit var list:ArrayList<Song>
    @Inject lateinit var mediaPlayer: MediaPlayer
    lateinit var networkViewModel: NetworkViewModel
    lateinit var mediaViewModel: MediaViewModel
    lateinit var seekBar:SeekBar
    var start:Boolean=false
    lateinit var songSp:Song
    var songIndex:Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        mediaViewModel = ViewModelProvider(this)[MediaViewModel::class.java]
        makeStatusBarTransparent()
        (application as BhaktApplication).bhaktComponent.inject(this)
        list = ArrayList()
        binding.backBtn.setOnClickListener {
            finish()
        }

        if (mediaPlayer.isPlaying){
            setUpBottomSheet()
        }

        networkViewModel.fetchGaneshSong()
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        mediaPlayer.setOnPreparedListener {
            setUpBottomSheet()
            binding.shimmer.root.visibility =  View.GONE
            binding.recycler.visibility = View.VISIBLE
            if (start) startPlayer()
        }

        mediaPlayer.setOnErrorListener { _, _, _ ->
            binding.shimmer.root.visibility =  View.VISIBLE
            binding.recycler.visibility = View.GONE
            true
        }

        mediaPlayer.setOnCompletionListener {
            Glide.with(binding.playerSheet.playPauseIv)
                .load(R.drawable.play_ico)
                .into(binding.playerSheet.playPauseIv)
        }
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerSheet.root)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, i: Int) {

            }

            override fun onSlide(bottomSheet: View, v: Float) {
               binding.ll1.alpha = 1-v

                val dp = (90*v).toInt()
               binding.playerSheet.root.setPadding(0,dp,0,0)
            }
        })


        seekBar = binding.playerSheet.seekBar

        if(mediaPlayer.isPlaying){
            Glide.with(binding.playerSheet.playPauseIv)
                .load(R.drawable.pause_ico)
                .into(binding.playerSheet.playPauseIv)
            val songStr = MySp.get(SONG,this)
            if(songStr!="-1"){
                songSp = Gson().fromJson(songStr,Song::class.java)
                binding.playerSheet.nameTv.text = songSp.name
                binding.playerSheet.artistTv.text = songSp.singerName.ifBlank { "Mixed" }
            }

        }



        binding.playerSheet.playPauseIv.setOnClickListener {
            handleAudio()
        }


        binding.playerSheet.forwardsIv.setOnClickListener {
            playNextSong()
        }

        binding.playerSheet.backwardsIv.setOnClickListener {
            playPreviousSong()
        }



        seekBar.max = mediaPlayer.duration
       val handler =  Handler(Looper.myLooper()!!)
           handler.postDelayed(object :Runnable{
               override fun run() {
                   try {
                       seekBar.progress = mediaPlayer.currentPosition
                       handler.postDelayed(this,500)
                   }catch(e:Exception){
                       seekBar.progress = 0
                   }
               }
           },0)


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun playPreviousSong() {
        songIndex?.let {
            if(it!=0){
                songIndex = songIndex!! - 1
                setSong(list[songIndex!!],true)
            }else{
                setSong(list[list.lastIndex],true)
            }
        }
    }

    private fun playNextSong() {
        songIndex?.let {
            if(it!=list.lastIndex){
                songIndex = songIndex!! + 1
                setSong(list[songIndex!!],true)
            }else{
                setSong(list[0],true)
            }
        }
    }

    private fun handleAudio() {
        if (mediaPlayer.isPlaying){
            Glide.with(this)
                .load(R.drawable.play_ico)
                .into(binding.playerSheet.playPauseIv)
            mediaPlayer.pause()
        }else{
            Glide.with(binding.playerSheet.playPauseIv)
                .load(R.drawable.pause_ico)
                .into(binding.playerSheet.playPauseIv)
            startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        Glide.with(binding.playerSheet.playPauseIv)
            .load(R.drawable.pause_ico)
            .into(binding.playerSheet.playPauseIv);
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
                      list.sortBy { song -> song.name }
                      binding.recycler.adapter = ListCardAdapter(list)
                      binding.playerSheet.recycler.adapter = ListCardAdapter(list)
                      if(list.isNotEmpty())
                      setSong(list[0],false)
                    }
                }
                is Resource.Error ->{
                    binding.shimmer.root.stopShimmer()
                    Snackbar.make(binding.root,"Error Loading Data", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{
                    binding.shimmer.root.startShimmer()
                }
            }
        }



        lifecycleScope.launchWhenStarted {
            mediaViewModel.mediaPlayerState.observe(this@SongListActivity){
                setSong(it,true)
            }

            mediaViewModel.songIndexState.observe(this@SongListActivity){
                songIndex = it
            }
        }
    }

    private fun setSong(song:Song,start:Boolean) {
        if (song.url.isNotBlank()){
            binding.playerSheet.nameTv.text = song.name
            binding.playerSheet.nameTv.isSelected = true
            binding.playerSheet.artistTv.text = song.singerName.ifBlank { "Mixed" }
            try{
                lifecycleScope.launch(Dispatchers.IO){
                    this@SongListActivity.start = start
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(song.url)
                    mediaPlayer.prepareAsync()
                    MySp.insert(SONG,Gson().toJson(song),this@SongListActivity)
                }
            }catch(e:Exception){
               // binding.shimmer.root.visibility =  View.VISIBLE
                binding.shimmer.root.stopShimmer()
                //binding.recycler.visibility = View.GONE
            }
        }
    }
}