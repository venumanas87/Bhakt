package xyz.v.bhakt

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.v.bhakt.adapter.ListCardAdapter
import xyz.v.bhakt.application.BhaktApplication
import xyz.v.bhakt.databinding.ActivityMainBinding
import xyz.v.bhakt.fragments.HomeFragment
import xyz.v.bhakt.models.Song
import xyz.v.bhakt.utils.Constants
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.utils.MySp
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.NetworkViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var mediaPlayer: MediaPlayer
    lateinit var seekBar:SeekBar
    lateinit var nvm:NetworkViewModel
    private var list:ArrayList<Song> = ArrayList()
    var songIndex:Int? = 0
    var start:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent()
        nvm = ViewModelProvider(this)[NetworkViewModel::class.java]
        (application as BhaktApplication).bhaktComponent.inject(this)
        transactFragment(HomeFragment())
        checkMediaPlay()
        setListeners()

    }

    private fun setListeners() {
        mediaPlayer.setOnPreparedListener {
            if (start) startPlayer()
        }

        mediaPlayer.setOnCompletionListener {
            Glide.with(binding.playerSheet.playPauseIv)
                .load(R.drawable.play_ico)
                .into(binding.playerSheet.playPauseIv)
        }
    }


    private fun startPlayer() {
        mediaPlayer.start()
        Glide.with(binding.playerSheet.playPauseIv)
            .load(R.drawable.pause_ico)
            .into(binding.playerSheet.playPauseIv);
    }

    private fun checkMediaPlay() {
        if(mediaPlayer.isPlaying){
          binding.coordinator.visibility = View.VISIBLE
          setUpBottomSheet()
        }
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerSheet.root)
        nvm.fetchGaneshSong()
        setObservers()
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, i: Int) {

            }

            override fun onSlide(bottomSheet: View, v: Float) {
                val dp = (90*v).toInt()
                binding.playerSheet.root.setPadding(0,dp,0,0)
            }
        })
        val songStr = MySp.get(Constants.SONG,this)
        if(songStr!="-1"){
            val song: Song = Gson().fromJson(songStr, Song::class.java)
            binding.playerSheet.nameTv.text = song.name
            binding.playerSheet.artistTv.text = song.singerName.ifBlank { "Mixed" }
        }

        val view = binding.root.findViewById<View>(R.id.player_sheet)

        val playPauseIv: ImageView = view.findViewById(R.id.play_pause_iv)
        seekBar = view.findViewById(R.id.seekBar)
        Glide.with(playPauseIv)
            .load(R.drawable.pause_ico)
            .into(playPauseIv)

        binding.playerSheet.forwardsIv.setOnClickListener {
            playNextSong()
        }

        binding.playerSheet.backwardsIv.setOnClickListener {
            playPreviousSong()
        }

        playPauseIv.setOnClickListener {
            if (mediaPlayer.isPlaying){
                Glide.with(playPauseIv)
                    .load(R.drawable.play_ico)
                    .into(playPauseIv)
                mediaPlayer.pause()
            }else{
                Glide.with(playPauseIv)
                    .load(R.drawable.pause_ico)
                    .into(playPauseIv)
                mediaPlayer.start()
            }
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

    private fun setObservers() {
        nvm.ganeshSongLiveData.observe(this) { res ->
            when(res){
                is Resource.Success->{
                    res.data?.let {
                        list.clear()
                        for(s:Song in it){
                            list.add(s)
                        }
                        list.sortBy { song -> song.name }
                        binding.playerSheet.recycler.adapter = ListCardAdapter(list)
                    }
                }
                is Resource.Error ->{

                }
                is Resource.Loading ->{
                }
            }
        }
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


    private fun setSong(song:Song,start:Boolean) {
        if (song.url.isNotBlank()){
            binding.playerSheet.nameTv.text = song.name
            binding.playerSheet.nameTv.isSelected = true
            binding.playerSheet.artistTv.text = song.singerName.ifBlank { "Mixed" }
            try{
                lifecycleScope.launch(Dispatchers.IO){
                    this@MainActivity.start = start
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(song.url)
                    mediaPlayer.prepareAsync()
                    MySp.insert(Constants.SONG,Gson().toJson(song),this@MainActivity)
                }
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun transactFragment(frag: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container,frag)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        checkMediaPlay()
    }



}
