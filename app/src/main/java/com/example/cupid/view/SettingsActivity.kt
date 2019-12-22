package com.example.cupid.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cupid.R
import kotlinx.android.synthetic.main.settings_activity.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.cupid.view.utils.getAvatarFromId
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ImageView
import com.example.cupid.controller.ControllerModule.settingsController
import com.example.cupid.model.ModelModule
import com.example.cupid.view.views.SettingsView


class SettingsActivity :
    AppCompatActivity(), AdapterView.OnItemSelectedListener,
    SettingsView {

    private val model = ModelModule.dataAccessLayer
    private val controller = settingsController()

    private var mName : String = ""
    private var mIconId : Int = 0

    private var mGender = ""
    private var mLookingFor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)


    }

    override fun onStart() {
        super.onStart()
        val modelData = controller.readUserInformation()

        //spinner_settings_icon.setBackgroundResource(getAvatarFromId(this,mIconId))

        setClickListeners()
        mIconId = modelData.first
        mName = modelData.second
        edittext_settings_name.setText(mName)
        spinner_settings_icon.setSelection(mIconId-1)
    }



    private fun setClickListeners(){

        button_settings_close.setOnClickListener {
            writeUserInformation()
            finish()
        }

        // Repeated, refactor this later

        val adapterIcon = CustomAdapter<String>(
            this,
            R.layout.item_settings_spinner,R.id.text_item_spinner_layout,
            arrayOf("1","2","3","4","5","6","7","8","9","10","11","12")
        )


        adapterIcon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapterGender = ArrayAdapter.createFromResource(
            this,
            R.array.settings_gender, android.R.layout.simple_spinner_item)

        adapterIcon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapterLookingFor = ArrayAdapter.createFromResource(
            this,
            R.array.settings_looking_for, android.R.layout.simple_spinner_item)

        adapterIcon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_settings_icon.adapter = adapterIcon
        //spinner_settings_gender.adapter = adapterGender
        //spinner_settings_looking_for.adapter = adapterLookingFor

        spinner_settings_icon.onItemSelectedListener = this
        //spinner_settings_gender.onItemSelectedListener = this
        //spinner_settings_looking_for.onItemSelectedListener = this

        edittext_settings_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
               mName = s.toString()
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, arg3: Long) {

        val value = parent.getItemAtPosition(position).toString()
        when (parent.id){
            R.id.spinner_settings_icon -> {
                mIconId = value.toInt()
                spinner_settings_icon.setBackgroundResource(getAvatarFromId(this,mIconId))
            }
            /*
            R.id.spinner_settings_gender -> {
                mGender = value
            }
            R.id.spinner_settings_looking_for -> {
                mLookingFor = value
            }*/
            else -> {}
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    private class CustomAdapter<T>(
        context: Context,
        layoutViewResourceId: Int,
        textViewResourceId: Int,
        objects: Array<String>
    ) : ArrayAdapter<String>(context, layoutViewResourceId, textViewResourceId, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            //val textView = view.findViewById<View>(android.R.id.text1) as TextView
            val textView = view.findViewById<View>(R.id.text_item_spinner_layout) as TextView
            val imageView =  view.findViewById<View>(R.id.image_item_spinner_layout) as ImageView
            imageView.setImageResource(0)
            textView.text = ""


            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            //val textView = view.findViewById<View>(android.R.id.text1) as TextView
            val textView = view.findViewById<View>(R.id.text_item_spinner_layout) as TextView
            textView.text = ""
            val imageView =  view.findViewById<View>(R.id.image_item_spinner_layout) as ImageView
            imageView.setImageResource(getAvatarFromId(context,position+1))



            return view
        }
    }

    override fun readUserInformation() {
        val res = controller.readUserInformation()
        mIconId = res.first
        mName = res.second
    }

    override fun writeUserInformation() {
        controller.writeUserInformation(mIconId, mName)

    }

    override fun onBackPressed() {
        writeUserInformation()
        finish()
    }
}