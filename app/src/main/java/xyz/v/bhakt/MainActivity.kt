package xyz.v.bhakt

import android.app.Activity
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import xyz.v.bhakt.application.BhaktApplication
import xyz.v.bhakt.databinding.ActivityMainBinding
import xyz.v.bhakt.fragments.HomeFragment
import xyz.v.bhakt.models.HomeRc
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.viewmodel.NetworkViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    lateinit var networkViewModel: NetworkViewModel

    lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var mediaPlayer: MediaPlayer
    lateinit var seekBar:SeekBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent()
        (application as BhaktApplication).bhaktComponent.inject(this)
        transactFragment(HomeFragment())
        binding.bubbleTabBar.setSelected(0,true)

        checkMediaPlay()

    }

    private fun checkMediaPlay() {
        if(mediaPlayer.isPlaying){
          binding.coordinator.visibility = View.VISIBLE
            setUpBottomSheet()
        }
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playerSheet.root)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, i: Int) {

            }

            override fun onSlide(bottomSheet: View, v: Float) {
               if (v>0.3){
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                println("venu slide $v")
            }
        })

        val view = binding.root.findViewById<View>(R.id.player_sheet)

        val playPauseIv: ImageView = view.findViewById(R.id.play_pause_iv)
        val backwardsIv: ImageView = view.findViewById(R.id.backwardsIv)
        val forwardsIv: ImageView = view.findViewById(R.id.forwardsIv)
        seekBar = view.findViewById(R.id.seekBar)
        Glide.with(playPauseIv)
            .load(R.drawable.pause_ico)
            .into(playPauseIv)

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
