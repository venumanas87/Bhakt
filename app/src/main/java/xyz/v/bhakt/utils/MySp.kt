package xyz.v.bhakt.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class MySp {


    companion object{
        val pref_name:String = "MYDB7389"

        fun insert(key:String,value:String,context: Context){
            val sharedPreference =  context.getSharedPreferences(pref_name,Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString(key,value)
            editor.apply()
        }

        fun get(key:String,context: Context):String{
            val sharedPreference =  context.getSharedPreferences(pref_name,Context.MODE_PRIVATE)
            return sharedPreference.getString(key,"-1").toString()
        }

    }


}