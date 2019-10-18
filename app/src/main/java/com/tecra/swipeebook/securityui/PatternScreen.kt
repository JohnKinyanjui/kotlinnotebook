package com.tecra.swipeebook.securityui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.tecra.swipeebook.R
import com.tecra.swipeebook.Settings.SaveSettings
import es.dmoral.toasty.Toasty
import io.paperdb.Paper
import com.tecra.swipeebook.noteui.Locker_note


class PatternScreen : AppCompatActivity() {
    private val pattern_string : String = "pattern_key"
    var final_pattern : String = "";
    lateinit var sharedpref: SaveSettings
    lateinit var mAdView : AdView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        Paper.init(this@PatternScreen)
        val save_pattern : String? = Paper.book().read(pattern_string)

        if (save_pattern != null && !save_pattern.equals("null")){
            setContentView(R.layout.activity_pattern_screen)
            val mpattern = findViewById<View>(R.id.pattern)  as PatternLockView
            mpattern.addPatternLockListener(object: PatternLockViewListener {
                override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                    final_pattern = PatternLockUtils.patternToString(mpattern,pattern)
                    if (final_pattern.equals(save_pattern)) {
                        Toasty.success(
                            applicationContext,
                            "Pattern correct",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        val intent = Intent(this@PatternScreen, Locker_note::class.java)
                        startActivity(intent)

                    } else{
                        Toasty.error(
                            applicationContext,
                            "Pattern incorrect",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                override fun onCleared() {

                }

                override fun onStarted() {

                }

                override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

                }

            })
            val reset = findViewById<View>(R.id.fyou) as TextView
            reset.setOnClickListener {


                Paper.book().delete(pattern_string)
                val intent2 = Intent(this@PatternScreen,PatternScreen::class.java)
                startActivity(intent2)
            }
            MobileAds.initialize(this) {}

            mAdView = findViewById(R.id.ads)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)

        }
        else{
            setContentView(R.layout.activity_pattern)
            intent.getStringExtra("re2")
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
                Paper.book().write(pattern_string,final_pattern)
                Toasty.success(
                    applicationContext,
                    "Saved Pattern",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                val intent = Intent(this@PatternScreen,
                    PatternScreen::class.java)
                startActivity(intent)
            }
        }


        }

    }



