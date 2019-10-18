package com.tecra.swipeebook.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.tecra.swipeebook.R
import com.tecra.swipeebook.Settings.SaveSettings
import kotlinx.android.synthetic.main.activity_broswer.*
@SuppressLint("StaticFieldLeak")
lateinit var sharedpref: SaveSettings
class BroswerActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broswer)

        //webview
        val webview = findViewById<View>(R.id.webview) as WebView
        webview.settings.allowContentAccess = true
        webview.loadUrl("www.google.com/m?q=")
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.javaScriptEnabled = true
        webview.settings.databaseEnabled = true

        webview.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                progressBar.progress = 0
                webUri.setText(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.VISIBLE
                progressBar.progress = 0
            }
        }

        //Download Manager
        webview.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val request = DownloadManager.Request(
                Uri.parse(url)
            )
            request.setMimeType(mimeType)
            val cookies = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading File...")
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                    url, contentDisposition, mimeType
                )
            )

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()


        })
        val web = intent.getStringExtra("web")
        webview.loadUrl("www.google.com/m?q=$web")


       webUri.setOnEditorActionListener { textView, i, keyEvent ->
           if (i == EditorInfo.IME_ACTION_GO){
            val web =  webUri.text.toString()
               webview.loadUrl("http://$web")
           }
           true
       }

        val bottomNavigationViewEx = findViewById<View>(R.id.bnve) as BottomNavigationViewEx
        bottomNavigationViewEx.setTextVisibility(false)
        bottomNavigationViewEx.enableItemShiftingMode(false)
        bottomNavigationViewEx.enableAnimation(false)
        bottomNavigationViewEx.enableShiftingMode(false)

        bottomNavigationViewEx.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.navigation_back -> {
                   webview.goBack()
                }
                R.id.navigation_forward -> {
                   webview.goForward()
                }
                R.id.navigation_home -> {
                   webview.loadUrl("https://www.google.com/")
                }
                R.id.navigation_search -> {

                }
                R.id.stop -> {
                    webview.stopLoading()

                }
            }
            true
        }

    }
}
