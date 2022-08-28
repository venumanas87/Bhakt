package xyz.v.bhakt.fragments

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.ak1.BubbleTabBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.v.bhakt.R
import xyz.v.bhakt.SplashActivity
import xyz.v.bhakt.adapter.RcHomeAdapter
import xyz.v.bhakt.models.HomeRc
import xyz.v.bhakt.utils.Constants.Companion.mandirUrl
import xyz.v.bhakt.utils.Constants.Companion.mantraUrl
import xyz.v.bhakt.utils.Constants.Companion.panchangUrl
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.NetworkViewModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    lateinit var volumeIV:ImageView
    lateinit var mantraIV:ImageView
    lateinit var mandirIV:ImageView
    lateinit var panchangIV:ImageView
    lateinit var progressBar: ProgressBar
    lateinit var quoteTV: TextView
    var urlQuote = ""
    private val player = MediaPlayer()
    lateinit var networkViewModel: NetworkViewModel
    lateinit var recyclerView: RecyclerView
    var storageRef = Firebase.storage.reference
    val homeRef = storageRef.child("home")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        progressBar = view.findViewById(R.id.progress_circular)
        quoteTV = view.findViewById(R.id.quote_tv)
        recyclerView = view.findViewById(R.id.recycler)
        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        setupListeners()
        setObservers()
        networkViewModel.fetchQuote()
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
/*
        homeRef.listAll().addOnSuccessListener { v->
            println("success venu" + v.items.size)
            for (i in 0 until v.items.size){
                v.items[i].downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful){
                        al.add(HomeRc(v.items[i].name.split(".")[0],it.result.toString()))
                        println(it.result.toString() + " venu")
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }*/

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
                }
            }
        }
    }

    private fun initPlayer(url: String) {
        urlQuote = url
        lifecycleScope.launch(Dispatchers.IO){
            val uri: Uri = Uri.parse(url)
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            player.setDataSource(requireContext(), uri)
            player.prepare()
        }
    }



    private fun setupListeners() {


        player.setOnPreparedListener {
            progressBar.visibility = View.GONE
        }

        player.setOnCompletionListener {
            volumeIV.setImageResource(R.drawable.ic_baseline_volume_up_24)
        }

        player.setOnBufferingUpdateListener { mp, percent ->
            if (percent>=100){
                progressBar.visibility = View.GONE
            }else{
                progressBar.visibility = View.VISIBLE
            }
        }
    }
    
    
    


    private fun playAudio(){
        try {
            if (player.isPlaying){
                player.pause()
                volumeIV.setImageResource(R.drawable.ic_baseline_volume_up_24)
            }else{
                volumeIV.setImageResource(R.drawable.ic_baseline_volume_off_24)
                player.start()

            }
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}