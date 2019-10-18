package com.tecra.swipeebook.model

class PrivateNote {
    var privateID:Int? = null
    var privateName:String? = null
    var privateDes:String? = null

    constructor(privateID:Int,privateName:String,privateDes:String){
        this.privateID=privateID
        this.privateName=privateName
        this.privateDes=privateDes

    }


}