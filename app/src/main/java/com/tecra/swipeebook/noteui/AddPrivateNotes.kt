package com.tecra.swipeebook.noteui

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import com.tecra.swipeebook.activity.BroswerActivity
import com.tecra.swipeebook.R
import com.tecra.swipeebook.Settings.SaveSettings
import com.tecra.swipeebook.sql_manager.privateManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_private_notes.*

class AddPrivateNotes : AppCompatActivity() {
    val dbTable="PrivateNotes"
    lateinit var sharedpref: SaveSettings
    var id=0
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_private_notes)

        setSupportActionBar(toolbar_add1)
        toolbar_add1.setNavigationIcon(R.drawable.back_white)
        toolbar_add1.setNavigationOnClickListener {
            onBackPressed()
        }

        val dot = findViewById<View>(R.id.Pdots) as ImageView
        dot.setOnClickListener {
                view: View? -> Dots()
        }
        val save = findViewById<View>(R.id.Psave) as Button
        save.setOnClickListener {
                view: View? -> buAdd()
        }


        try{
            var bundle:Bundle= intent.extras!!
            id=bundle.getInt("ID",0)
            if(id!=0) {
                    ttle.setText(bundle.getString("name") )
                des.setText(bundle.getString("des") )

            }
        }catch (ex:Exception){}


    }

    private fun Dots() {
        val wrapper = ContextThemeWrapper(this, R.style.text_size)
        val popupMenu = PopupMenu(wrapper, Pdots, Gravity.START)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.wb ->{
                    val des = findViewById<View>(R.id.des) as EditText
                    val t2 =des.text.toString()
                    val intent = Intent(this@AddPrivateNotes, BroswerActivity::class.java)
                    intent.putExtra("web", t2)
                    startActivity(intent)
                    true
                }
                R.id.share -> {
                    val sharingIntent =  Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    val sharebody = sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "APP NAME (Open it in Google Play Store to Download the Application)");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

                    true
                }

                else -> false
            }
        }
        popupMenu.inflate(R.menu.dots_menu)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popupMenu.show()
        }


    }

    private fun buAdd() {

        var dbManager= privateManager(this)

        var values= ContentValues()
        values.put("Title",ttle.text.toString())
        values.put("Description",des.text.toString())


        if(id==0) {
            val ID = dbManager.Insert(values)
            if (ID > 0) {
                Toasty.success(applicationContext, "Note is Added", Toast.LENGTH_SHORT, true).show()
                finish()
            } else {
                Toasty.error(applicationContext, "Note is not Added", Toast.LENGTH_SHORT, true).show()
            }
        }else{
            var selectionArs= arrayOf(id.toString())
            val ID = dbManager.Update(values,"ID=?",selectionArs)
            if (ID > 0) {
                Toasty.success(applicationContext, "Note is Added", Toast.LENGTH_SHORT, true).show()
                finish()
            } else {
                Toasty.error(applicationContext, "Note is not Added", Toast.LENGTH_SHORT, true).show()
            }
        }

    }
}
