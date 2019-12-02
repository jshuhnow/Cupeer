package com.example.cupid.view

import android.net.MacAddress

interface LoginView {
    fun getName(): String
    fun getAge(): Int
    fun getAvartarrId() : Int
    fun getPhotoPath(): String
    fun getBio(): String
    fun getMaccAddress(): MacAddress
}