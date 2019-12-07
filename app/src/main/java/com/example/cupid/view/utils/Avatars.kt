package com.example.cupid.view.utils

import android.content.Context

fun getAvatarFromId(context: Context, id: Int): Int{
    return context.resources.getIdentifier("drawable/ic_element_"+ id.toString(), null, context.packageName);
}