package com.tecra.swipeebook.model

class Note_Locker{

    var lockID:Int?=null
    var lockName:String?=null
    var lockDes:String?=null

    constructor(lockID:Int,lockName:String,lockDes:String){
        this.lockID=lockID
        this.lockName=lockName
        this.lockDes=lockName
    }

}