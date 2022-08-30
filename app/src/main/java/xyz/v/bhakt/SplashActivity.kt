package xyz.v.bhakt

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.v.bhakt.databinding.ActivitySpalshBinding
import xyz.v.bhakt.utils.Constants
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.utils.Constants.Companion.quote
import xyz.v.bhakt.utils.Constants.Companion.url
import xyz.v.bhakt.utils.Resource
import xyz.v.bhakt.viewmodel.NetworkViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    lateinit var bind:ActivitySpalshBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySpalshBinding.inflate(layoutInflater)
        setContentView(bind.root)
        makeStatusBarTransparent()
       Handler(Looper.myLooper()!!).postDelayed({
              startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        },3500)


    }
}