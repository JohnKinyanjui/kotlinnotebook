package com.tecra.swipeebook.noteui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tecra.swipeebook.R
import com.tecra.swipeebook.sql_manager.lockManager
import com.tecra.swipeebook.model.Note_Locker
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.locker_layout.view.*
import kotlinx.android.synthetic.main.locker_note.*

class Locker_note : AppCompatActivity() {
    var listNote=ArrayList<Note_Locker>()
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locker_note)


        setSupportActionBar(locker_tool)

        val add = findViewById<View>(R.id.locker_Add) as FloatingActionButton
        add.setOnClickListener {
            var intent= Intent(this, add_locker::class.java)
            startActivity(intent)
        }

        //Load from DB
        LoadQuery("%")
    }

    override  fun onResume() {
        super.onResume()
        LoadQuery("%")

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()


    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onRestart() {
        super.onRestart()

    }


    fun LoadQuery(title:String){



        var dbManager= lockManager(this)
        val projections= arrayOf("ID","Title","Description")
        val selectionArgs= arrayOf(title)
        val cursor=dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNote.clear()
        if(cursor.moveToFirst()){

            do{
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title=cursor.getString(cursor.getColumnIndex("Title"))
                val Description=cursor.getString(cursor.getColumnIndex("Description"))

                listNote.add(Note_Locker(ID, Title, Description))

            }while (cursor.moveToNext())
        }

        var myNotesAdapter= MyNotesAdpater(this,listNote)
        lock_list.adapter=myNotesAdapter


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.note_locker, menu)

        val sv: SearchView = menu.findItem(R.id.app_bar_search1).actionView as SearchView
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
        var listNotesAdpater=ArrayList<Note_Locker>()
        var context: Context?=null
        constructor(context: Context, listNotesAdpater:ArrayList<Note_Locker>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }

        @SuppressLint("ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.locker_layout,null)
            var myNote=listNotesAdpater[p0]

            myView.tvTitle1.text=myNote.lockName
            myView.description.text=myNote.lockDes

            myView.ivDelete1.setOnClickListener( View.OnClickListener {
                var dbManager= lockManager(this.context!!)
                val selectionArgs= arrayOf(myNote.lockID.toString())
                dbManager.Delete("ID=?",selectionArgs)
                LoadQuery("%")
            })
            myView.ivEdit1.setOnClickListener( View.OnClickListener{

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


    fun GoToUpdate(note: Note_Locker){
        var intent=  Intent(this, add_locker::class.java)
        intent.putExtra("ID",note.lockID)
        intent.putExtra("name",note.lockName)
        intent.putExtra("des",note.lockDes)
        startActivity(intent)
    }


}


