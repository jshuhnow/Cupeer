package com.example.cupid.view.utils

import android.content.Context
import android.util.Log

fun getAvatarFromId(context: Context, id: Int): Int{

    return context.resources.getIdentifier("drawable/ic_element_"+ id.toString(), null, context.packageName)
}