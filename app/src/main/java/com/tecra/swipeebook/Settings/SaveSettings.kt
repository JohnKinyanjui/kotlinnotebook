package com.tecra.swipeebook.Settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

open class SaveSettings {
   var context: Context? = null
    var sharedpref: SharedPreferences? = null

    constructor(context: Context){
        this.context = context
        sharedpref = context.getSharedPreferences("myPref",Context.MODE_PRIVATE)
    }
    @SuppressLint("CommitPrefEdits")
     fun setNightMode(state: Boolean){
        val editor = sharedpref!!.edit()
        editor.putBoolean("Nightmode",state)
        editor.commit()
    }

    fun loadNightModeState(): Boolean {
        val state : Boolean = sharedpref!!.getBoolean("Nightmode", false)
        return state;
    }
}