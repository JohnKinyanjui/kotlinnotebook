package com.tecra.swipeebook.noteui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.tecra.swipeebook.sql_manager.DbManager
import kotlinx.android.synthetic.main.content_note.*
import kotlinx.android.synthetic.main.ticcket.view.*
import android.widget.Toast
import com.google.android.gms.ads.*
import com.infideap.drawerbehavior.AdvanceDrawerLayout
import com.tecra.swipeebook.activity.BroswerActivity
import com.tecra.swipeebook.securityui.PatternScreen
import com.tecra.swipeebook.R
import com.tecra.swipeebook.Settings.SaveSettings
import com.tecra.swipeebook.Settings.SettingsActivity
import com.tecra.swipeebook.model.Note
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.tecra.swipeebook.Ads
import java.util.*
import kotlin.collections.ArrayList


class NoteActivity : AppCompatActivity() {
    var listNotes=ArrayList<Note>()
    var currentNativeAd: UnifiedNativeAd? = null
    val LOG_TAG = ""
    private lateinit var adRequest : AdRequest
    private var mInterstitialAd: InterstitialAd? = null
    var listNotesAdpater=ArrayList<Note>()
    lateinit var sharedpref: SaveSettings
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)




        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        refresh_button.setOnClickListener { refreshAd() }

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
            videostatus_text.text = String.format(
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
                    refresh_button.isEnabled = true
                    videostatus_text.text = "Video status: Video playback has ended."
                    super.onVideoEnd()
                }
            }
        } else {
            videostatus_text.text = "Video status: Ad does not contain a video asset."
            refresh_button.isEnabled = true
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    @SuppressLint("ResourceAsColor")
    private fun refreshAd() {
        refresh_button.isEnabled = false

        val builder = AdLoader.Builder(this, Companion.ADMOB_AD_UNIT_ID)

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            val adView = layoutInflater
                .inflate(R.layout.ad_unified, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            ad_frame.removeAllViews()
            ad_frame.addView(adView)
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(start_muted_checkbox.isChecked)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                refresh_button.isEnabled = true
                Toast.makeText(this@NoteActivity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

        videostatus_text.text = ""







        val toolbar1 = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
       toolbar1.setNavigationIcon(R.drawable.nav)
        toolbar1.setTitleTextColor(R.color.grey)
        toolbar1.popupTheme

        val drawerLayout: AdvanceDrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.useCustomBehavior(Gravity.START);
        drawerLayout.setRadius(Gravity.START, 40f);
        val navView: NavigationView = findViewById(R.id.nav_view)
        toolbar.setNavigationOnClickListener {
           drawerLayout.openDrawer(navView)
        }


        val addNotes = findViewById<View>(R.id.add_notes) as FloatingActionButton
        addNotes.setOnClickListener {
            val add = Intent(this@NoteActivity, AddNotes::class.java)
            startActivity(add)
        }

        navView.setNavigationItemSelectedListener {menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.home -> {

                }
                R.id.p_notes -> {
                    val p = Intent(this@NoteActivity, PatternScreen::class.java)
                    startActivity(p)
                }
                R.id.web ->{
                    val web = Intent(this@NoteActivity, BroswerActivity::class.java)
                    startActivity(web)
                }
                R.id.set ->{
                    val inten = Intent(this@NoteActivity, SettingsActivity::class.java)
                    startActivity(inten)

                }
                R.id.nav_share ->{
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.aptoide.com/store/apps/details?id=com.google.android.apps.plus"
                    )
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
                R.id.ad_video -> {
                    val inten1 = Intent(this@NoteActivity, Ads::class.java)
                    startActivity(inten1)

                }}
            true

        }
        //Load from DB
        LoadQuery("%")
    }





    override  fun onResume() {
        super.onResume()
        LoadQuery("%")

    }


    fun LoadQuery(title:String){



        var dbManager= DbManager(this)
        val projections= arrayOf("ID","Title","Description")
        val selectionArgs= arrayOf(title)
        val cursor=dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        if(cursor.moveToFirst()){

            do{
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title=cursor.getString(cursor.getColumnIndex("Title"))
                val Description=cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            }while (cursor.moveToNext())
        }

        var myNotesAdapter= MyNotesAdpater(this, listNotes)
        listview.adapter=myNotesAdapter


    }


    @SuppressLint("ResourceAsColor")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.note_menu, menu)

        val sv: SearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        val sm= getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                LoadQuery("%"+ query +"%")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


        return super.onCreateOptionsMenu(menu)
    }



    inner class  MyNotesAdpater: BaseAdapter {
        var listNotesAdpater=ArrayList<Note>()
        var context: Context?=null
        constructor(context: Context, listNotesAdpater:ArrayList<Note>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }




        @SuppressLint("ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.ticcket,null)
            var myNote=listNotesAdpater[p0]
            myView.tvTitle.text=myNote.nodeName
            myView.tvDes.text=myNote.nodeDes
            myView.ivDelete.setOnClickListener( View.OnClickListener {
                var dbManager= DbManager(this.context!!)
                val selectionArgs= arrayOf(myNote.nodeID.toString())
                dbManager.Delete("ID=?",selectionArgs)
                LoadQuery("%")
            })
            myView.ivEdit.setOnClickListener( View.OnClickListener{

                GoToUpdate(myNote)

            })
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listNotesAdpater.size

        }
        fun onNaviagationItemSelected(menuItem: MenuItem) {
         when (menuItem.itemId){

  }
   }

    }
    fun GoToUpdate(note: Note){
        var intent=  Intent(this, AddNotes::class.java)
        intent.putExtra("ID",note.nodeID)
        intent.putExtra("name",note.nodeName)
        intent.putExtra("des",note.nodeDes)
        startActivity(intent)
    }

    companion object {
        const val ADMOB_AD_UNIT_ID = "ca-app-pub-4902549008382464/6534338785"
    }


}

