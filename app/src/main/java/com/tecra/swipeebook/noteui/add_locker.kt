package com.tecra.swipeebook.noteui

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.tecra.swipeebook.R
import com.tecra.swipeebook.Settings.SaveSettings
import com.tecra.swipeebook.sql_manager.lockManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_locker.*

class add_locker : AppCompatActivity() {
    val dbTable="Notes"
    var id=0
    lateinit var sharedpref: SaveSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpref = object : SaveSettings(applicationContext){}
        if (sharedpref.loadNightModeState() == true){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_locker)

        val back = findViewById<View>(R.id.button1) as Button
        back.setOnClickListener {
            view: View? -> buAdd()
        }



        try{
            var bundle:Bundle= intent.extras!!
            id=bundle.getInt("ID",0)
            if(id!=0) {
                etTitle1.setText(bundle.getString("name") )
                etDes1.setText(bundle.getString("des") )

            }
        }catch (ex:Exception){}


    }

    private fun buAdd() {

        var dbManager= lockManager(this)

        var values= ContentValues()
        values.put("Title",etTitle1.text.toString())
        values.put("Description",etDes1.text.toString())


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


