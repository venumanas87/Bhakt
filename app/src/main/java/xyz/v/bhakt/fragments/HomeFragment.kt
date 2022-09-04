package xyz.v.bhakt.fragments

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.v.bhakt.R
import xyz.v.bhakt.adapter.RcHomeAdapter
import xyz.v.bhakt.models.HomeRc
import xyz.v.bhakt.utils.Constants.Companion.mandirUrl
import xyz.v.bhakt.utils.Constants.Companion.mantraUrl
import xyz.v.bhakt.utils.Constants.Companion.panchangUrl
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.NetworkViewModel


class HomeFragment : Fragment(){

    private lateinit var volumeIV:ImageView
    private lateinit var mantraIV:ImageView
    private lateinit var mandirIV:ImageView
    private lateinit var panchangIV:ImageView
    private lateinit var quoteTV: TextView
    private lateinit var audioRv: RelativeLayout
    private var urlQuote = ""
    lateinit var player: MediaPlayer
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var expandAnim:Animation
    private lateinit var shrinkAnim:Animation
    private var wasPlaying:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = MediaPlayer()
        expandAnim = AnimationUtils.loadAnimation(requireContext(),R.anim.expand_in)
        shrinkAnim = AnimationUtils.loadAnimation(requireContext(),R.anim.shrink)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        volumeIV = view.findViewById(R.id.vol_iv)
        mandirIV = view.findViewById(R.id.mandirIv)
        mantraIV = view.findViewById(R.id.mantraIv)
        panchangIV = view.findViewById(R.id.panchangIv)
        quoteTV = view.findViewById(R.id.quote_tv)
        audioRv = view.findViewById(R.id.audiorv)
        recyclerView = view.findViewById(R.id.recycler)
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        setupListeners()
        setObservers()
        lifecycleScope.launch(Dispatchers.IO) {
            networkViewModel.fetchQuote()
        }
        setUpRecyclerMenu()
        volumeIV.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                playAudio()
            }
        }
        loadImages()
        return view
    }

    private fun loadImages() {
        Glide.with(this)
            .load(Uri.parse(mantraUrl))
            .into(mantraIV)
        Glide.with(this)
            .load(Uri.parse(mandirUrl))
            .into(mandirIV)
        Glide.with(this)
            .load(Uri.parse(panchangUrl))
            .into(panchangIV)
    }

    private fun setUpRecyclerMenu() {

        val al:ArrayList<HomeRc> = ArrayList()
        al.add(HomeRc("Shuffle","https://cdn.pixabay.com/photo/2017/11/10/05/34/shuffle-2935464_960_720.png"))
        al.add(HomeRc("Durga Mata","https://2.bp.blogspot.com/-r5rETjiJjpQ/XMiZ8hPIByI/AAAAAAAAAKI/9HjADGfKzM4psodonacO9QbparDkyYmaACLcBGAs/s400/Durga%2BMaa%2BImages%2BDownload%2BHd.jpg"))
        al.add(HomeRc("Ganesh Ji","https://www.bhaktiphotos.com/wp-content/uploads/2020/12/First-God-Ganesh-Ji-Maharaj-hd-Wallpaper.jpg"))
        al.add(HomeRc("Hanuman Ji","https://www.bhaktiphotos.com/wp-content/uploads/2018/04/Mahabali-Veer-Hanuman-Bajrangbali-Ki.jpg"))

        recyclerView.adapter = RcHomeAdapter(al)
        }

    private fun setObservers() {
        networkViewModel.quotesLiveData.observe(requireActivity()) { res ->
            when(res){
                is Resource.Success->{
                    res.data?.let {
                        quoteTV.text = it.quote
                        initPlayer(it.url)
                    }
                }
                is Resource.Error ->{
                    Snackbar.make(requireView(),"Error Loading Data",Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{
                    audioRv.startAnimation(shrinkAnim)
                    audioRv.visibility = View.GONE
                }
            }
        }
    }

    private fun initPlayer(url: String) {
        try {
            urlQuote = url
            val uri: Uri = Uri.parse(url)
            context?.let {
                player.setDataSource(it, uri)
                player.prepareAsync()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }



    private fun setupListeners() {


        player.setOnPreparedListener {
            audioRv.visibility = View.VISIBLE
            audioRv.startAnimation(expandAnim)
        }

        player.setOnCompletionListener {
            volumeIV.setImageResource(R.drawable.speak)
            player.stop()
            player.reset()
            networkViewModel.fetchQuote()
        }
    }


    private fun playAudio(){
        try {
            if (player.isPlaying){
                player.pause()
                volumeIV.setImageResource(R.drawable.speak)
            }else{
                player.start()
                volumeIV.setImageResource(R.drawable.ic_baseline_volume_off_24)

            }
        } catch (e: Exception) {
            println(e.toString())
        }
    }


    override fun onPause() {
        super.onPause()
        player.pause()
        Glide.with(this)
            .load(R.drawable.speak)
            .into(volumeIV)
    }

    override fun onDetach() {
        super.onDetach()
        player.reset()
        player.release()
    }




}