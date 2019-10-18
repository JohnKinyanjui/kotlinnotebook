package com.tecra.swipeebook.securityui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.tecra.swipeebook.R
import com.tecra.swipeebook.Settings.SaveSettings
import es.dmoral.toasty.Toasty
import io.paperdb.Paper


class PatternActivity : AppCompatActivity() {
val pattern_string : String = "null"
    var final_pattern : String = "";
    lateinit var sharedpref: SaveSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_pattern)
        val mpattern = findViewById<View>(R.id.patter_lock_view)  as PatternLockView
        mpattern.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                final_pattern = PatternLockUtils.patternToString(mpattern,pattern)
            }

            override fun onCleared() {

            }

            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

            }
        })
        val mbutton = findViewById<View>(R.id.save_pattern)  as Button
        mbutton.setOnClickListener {
            Paper.init(this)
            Paper.book().write(pattern_string,final_pattern)
            Toasty.success(
                applicationContext,
                "Saved Pattern",
                Toast.LENGTH_SHORT,
                true
            ).show()
            onRestart()
        }
    }
    }


