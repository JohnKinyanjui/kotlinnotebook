package com.tecra.swipeebook.Settings


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.tecra.swipeebook.R
import kotlinx.android.synthetic.main.settings_activity.*
import java.util.*

@SuppressLint("ResourceAsColor")
class SettingsActivity : AppCompatActivity() {
    lateinit var sharedpref: SaveSettings
    var currentNativeAd: UnifiedNativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
         val toolbar = findViewById<View>(R.id.toolbar_settings) as Toolbar
        setSupportActionBar(toolbar_settings)
        toolbar.setTitleTextColor(R.color.white)
        toolbar.setNavigationIcon(R.drawable.back_white)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


           val darkmode = findViewById<View>(R.id.darkmode) as SwitchCompat
        if (sharedpref.loadNightModeState() == true){
           darkmode.isChecked = true
        }
        darkmode.setOnCheckedChangeListener (object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
              if (isChecked){
                sharedpref.setNightMode(true)
                  restartApp()
              }else{
                  sharedpref.setNightMode(false)
                  restartApp()
              }
            }

        })


            //retrieving data


        //retrieving data


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        refresh_button1.setOnClickListener { refreshAd() }

        refreshAd()
    }

    /**
     * Populates a [UnifiedNativeAdView] object with data from a given
     * [UnifiedNativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView the view to be populated
     */
    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // You must call destroy on old ads when you are done with them,
        // otherwise you will have a memory leak.
        currentNativeAd?.destroy()

        currentNativeAd = nativeAd
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            videostatus_text1.text = String.format(
                Locale.getDefault(),
                "Video status: Ad contains a %.2f:1 video asset.",
                vc.aspectRatio)

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    refresh_button1.isEnabled = true
                    videostatus_text1.text = "Video status: Video playback has ended."
                    super.onVideoEnd()
                }
            }
        } else {
            videostatus_text1.text = "Video status: Ad does not contain a video asset."
            refresh_button1.isEnabled = true
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    @SuppressLint("ResourceAsColor")
    private fun refreshAd() {
        refresh_button1.isEnabled = false

            val builder = AdLoader.Builder(this,
                ADMOB_AD_UNIT_ID
            )

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            val adView = layoutInflater
                .inflate(R.layout.ad_unified, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            ad_frame1.removeAllViews()
            ad_frame1.addView(adView)
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(start_muted_checkbox1.isChecked)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                refresh_button1.isEnabled = true
                Toast.makeText(this@SettingsActivity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

        videostatus_text1.text = ""

    }

    private fun restartApp() {
        val toast = Toast.makeText(applicationContext, "Restart App to apply changes",Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ADMOB_AD_UNIT_ID = "ca-app-pub-4902549008382464/6534338785"
    }
}


