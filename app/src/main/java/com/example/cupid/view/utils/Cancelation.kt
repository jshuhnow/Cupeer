package com.example.cupid.view.utils

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.example.cupid.R
import com.example.cupid.view.MainActivity
import kotlinx.android.synthetic.main.dialog_discovered.*
import kotlinx.android.synthetic.main.dialog_rejection.*

fun returnToMain(activity: Activity){
    val intents = Intent(activity, MainActivity::class.java)
    intents.addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
    )
    activity.startActivity(intents)
    activity.finish()
}

fun launchRejectedPopup(activity: Activity){
    val rejectionDialog = Dialog(activity)
    rejectionDialog.setContentView(R.layout.dialog_rejection)


    rejectionDialog.button_rejection_close.setOnClickListener{
        rejectionDialog.dismiss()
    }


    rejectionDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    rejectionDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    rejectionDialog.setOnDismissListener{
        returnToMain(activity)
    }

    rejectionDialog.setOnCancelListener{
        returnToMain(activity)
    }

    rejectionDialog.show()
}
