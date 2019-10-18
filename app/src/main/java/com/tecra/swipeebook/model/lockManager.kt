package com.tecra.swipeebook.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class lockManager {
    val lockName = "lockNotes"
    val lockTable = "Notes_Lock"
    private val lockID = "ID"
    private val lockTitle = "Title"
    private val lockDes = "Description"

    val dbVersion = 1;
     val sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + lockTable + "(" + lockID + " INTEGER PRIMARY KEY, " + lockTitle + " TEXT, " + lockDes + " TEXT);"
    var sqlDB: SQLiteDatabase? = null

    constructor(context: Context){
        var db=DatabaseHelperNotes(context)
        sqlDB =db.writableDatabase
    }

    inner class  DatabaseHelperNotes: SQLiteOpenHelper {
        var context:Context?=null

        constructor(context:Context):super(context,lockName,null,dbVersion){
            this.context=context
        }
        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context," database is created", Toast.LENGTH_LONG).show()

        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL("Drop table IF EXISTS " + lockTable)
        }

    }


    fun Insert(values: ContentValues):Long{

        val ID= sqlDB!!.insert(lockTable,"",values)
        return ID
    }
    fun  Query(projection:Array<String>,selection:String,selectionArgs:Array<String>,sorOrder:String): Cursor {

        val qb= SQLiteQueryBuilder()
        qb.tables=lockTable
        val cursor=qb.query(sqlDB,projection,selection,selectionArgs,null,null,sorOrder)
        return cursor
    }
    fun Delete(selection:String,selectionArgs:Array<String>):Int{

        val count=sqlDB!!.delete(lockTable,selection,selectionArgs)
        return  count
    }

    fun Update(values: ContentValues, selection:String, selectionargs:Array<String>):Int{

        val count=sqlDB!!.update(lockTable,values,selection,selectionargs)
        return count
    }
}