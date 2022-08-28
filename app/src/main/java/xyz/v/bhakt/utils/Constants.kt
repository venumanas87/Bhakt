package xyz.v.bhakt.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

class Constants {
    companion object{

        //https://yourimageshare.com/
        //venumanas87@gmail.com
        //123456789
        const val quote = "QUOTE"
        const val url = "URL"
        const val mandirUrl = "https://yourimageshare.com/ib/ddFqEFUj31.png"
        const val mantraUrl = "https://yourimageshare.com/ib/MN4EHOaWNU.png"
        const val panchangUrl = "https://yourimageshare.com/ib/4mFq6LcyR3.png"


         fun Activity.makeStatusBarTransparent() {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                statusBarColor = Color.TRANSPARENT
            }
        }
    }
}