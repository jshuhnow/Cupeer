package com.example.cupid.model.domain



data class Account (
    val macAddr : String = "",
    val name : String = "",
    val age : Int = 0,
    val avatarId : Int = 0,
    val photoPath : String = "",
    val bio : String = "",
    val pub_key : String = "",
    val pri_key : String = ""
)