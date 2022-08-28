package xyz.v.bhakt

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import xyz.v.bhakt.databinding.ActivityMainBinding
import xyz.v.bhakt.fragments.HomeFragment
import xyz.v.bhakt.models.HomeRc
import xyz.v.bhakt.utils.Constants.Companion.makeStatusBarTransparent
import xyz.v.bhakt.viewmodel.NetworkViewModel


class MainActivity : AppCompatActivity() {
    lateinit var networkViewModel: NetworkViewModel

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent()
        transactFragment(HomeFragment())
        binding.bubbleTabBar.setSelected(0,true)

    }



    private fun transactFragment(frag: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container,frag)
            commit()
        }
    }

}
