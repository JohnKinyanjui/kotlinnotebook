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
import com.tecra.swipeebook.sql_manager.privateManager
import kotlinx.android.synthetic.main.private_ticket.view.*
import android.widget.Toast
import com.tecra.swipeebook.R
import com.tecra.swipeebook.model.PrivateNote
import kotlinx.android.synthetic.main.activity_private.*


class PrivateNotes : AppCompatActivity() {
    var listNotes=ArrayList<PrivateNote>()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)

        val toolbar = findViewById<View>(R.id.private_toolbar) as? Toolbar
        setSupportActionBar(private_toolbar)
        toolbar?.setNavigationIcon(R.drawable.back_white)
        toolbar?.setTitleTextColor(R.color.grey)
        toolbar?.popupTheme

        toolbar?.setNavigationOnClickListener {
          onBackPressed()
        }


        val addNotes = findViewById<View>(R.id.private_add_notes) as FloatingActionButton
        addNotes.setOnClickListener {
            val add = Intent(this@PrivateNotes, AddPrivateNotes::class.java)
            startActivity(add)
        }


        //Load from DB
        LoadQuery("%")
    }



    override  fun onResume() {
        super.onResume()
        LoadQuery("%")

    }


    fun LoadQuery(title:String){



        var dbManager= privateManager(this)
        val projections= arrayOf("ID","Title","Description")
        val selectionArgs= arrayOf(title)
        val cursor=dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        if(cursor.moveToFirst()){

            do{
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title=cursor.getString(cursor.getColumnIndex("Title"))
                val Description=cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(PrivateNote(ID, Title, Description))

            }while (cursor.moveToNext())
        }

        var myNotesAdapter= MyNotesAdpater(this, listNotes)
        private_listview.adapter=myNotesAdapter


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
        var listNotesAdpater=ArrayList<PrivateNote>()
        var context: Context?=null
        constructor(context: Context, listNotesAdpater:ArrayList<PrivateNote>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }




        @SuppressLint("ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.private_ticket,null)
            var myNote=listNotesAdpater[p0]
            myView.PtvTitle.text=myNote.privateName
            myView.PtvDes.text=myNote.privateDes

            //delete
            myView.delete.setOnClickListener( View.OnClickListener {
                var dbManager= privateManager(this.context!!)
                val selectionArgs= arrayOf(myNote.privateID.toString())
                dbManager.Delete("ID=?",selectionArgs)
                LoadQuery("%")
            })
            myView.PivEdit.setOnClickListener( View.OnClickListener{

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
    }
    fun GoToUpdate(note: PrivateNote){
        val intent=  Intent(this, AddPrivateNotes::class.java)
        intent.putExtra("ID",note.privateID)
        intent.putExtra("name",note.privateName)
        intent.putExtra("des",note.privateDes)
        startActivity(intent)
    }


}

