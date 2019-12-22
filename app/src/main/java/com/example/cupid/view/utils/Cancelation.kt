package com.example.cupid.view.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.ModelModule
import com.example.cupid.view.MainActivity
import kotlinx.android.synthetic.main.dialog_discovered.*
import kotlinx.android.synthetic.main.dialog_instructions.*
import kotlinx.android.synthetic.main.dialog_rejection.*

fun returnToMain(activity: Activity){

    val model = ModelModule.dataAccessLayer

    val intents = Intent(activity, MainActivity::class.java)


    intents.addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP
             or Intent.FLAG_ACTIVITY_CLEAR_TASK
    )

    model.reset()
    Log.d("Test", model.getMessages().toString())
    activity.startActivity(intents)
    activity.finish()
}

fun launchRejectedPopup(activity: Activity) : Dialog {
    with (Dialog(activity)) {
        setContentView(R.layout.dialog_rejection)

        button_rejection_close.setOnClickListener{
            dismiss()
        }


        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setOnDismissListener{
            returnToMain(activity)
        }

        setOnCancelListener{
            returnToMain(activity)
        }

        show()
        return this
    }
}

fun launchInstructionPopup(context: Context, strings: List<String>){
    with(Dialog(context)) {
        setContentView(R.layout.dialog_instructions)
        this.setCancelable(false)
        text_instructions.text = strings[0]
        button_instructions_next.setOnClickListener {

            if (strings.size > 1){
                launchInstructionPopup(context,strings.drop(1))
            }
            dismiss()
        }

        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        show()
    }
}
