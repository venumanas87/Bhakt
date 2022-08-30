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
import kotlinx.coroutines.*
import xyz.v.bhakt.adapter.ListCardAdapter
import xyz.v.bhakt.application.BhaktApplication
import xyz.v.bhakt.databinding.ActivitySongListBinding
import xyz.v.bhakt.models.Song
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.NetworkViewModel
import java.lang.Runnable
import javax.inject.Inject

class SongListActivity : AppCompatActivity() {
    lateinit var backIv:ImageView
    lateinit var binding: ActivitySongListBinding
    lateinit var list:ArrayList<Song>
    @Inject lateinit var mediaPlayer: MediaPlayer
    lateinit var networkViewModel: NetworkViewModel
    lateinit var seekBar:SeekBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        makeStatusBarTransparent()
        (application as BhaktApplication).bhaktComponent.inject(this)
        list = ArrayList()

        //binding.recycler.adapter = ListCardAdapter(list)
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
        }

        mediaPlayer.setOnErrorListener { mp, what, extra ->
            binding.shimmer.root.visibility =  View.VISIBLE
            binding.recycler.visibility = View.GONE
            true
        }
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerSheet.root)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, i: Int) {

            }

            override fun onSlide(bottomSheet: View, v: Float) {
               binding.ll1.alpha = 1-v
            }
        })

        val view = binding.root.findViewById<View>(R.id.player_sheet)

        val playPauseIv:ImageView = view.findViewById(R.id.play_pause_iv)
        val backwardsIv:ImageView = view.findViewById(R.id.backwardsIv)
        val forwardsIv:ImageView = view.findViewById(R.id.forwardsIv)
        seekBar = view.findViewById(R.id.seekBar)

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


        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN;
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
                        if (!mediaPlayer.isPlaying){
                            try{
                                lifecycleScope.launch(Dispatchers.IO){
                                    mediaPlayer.reset()
                                    mediaPlayer.setDataSource(it[2].url)
                                    mediaPlayer.prepareAsync()
                                }
                            }catch(e:Exception){
                                binding.shimmer.root.visibility =  View.VISIBLE
                                binding.recycler.visibility = View.GONE
                            }
                        }else{
                            binding.shimmer.root.visibility =  View.GONE
                            binding.recycler.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error ->{
                    Snackbar.make(binding.root,"Error Loading Data", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{
                    binding.shimmer.root.startShimmer()
                }
            }
        }
    }
}