package com.example.cupid.view

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.cupid.R
import kotlinx.android.synthetic.main.settings_activity.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var name = ""
    private var iconId = 1
    private var gender = ""
    private var lookingFor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // TODO dummy values read in from model
        name = ""


        setClickListeners()
    }

    private fun setClickListeners(){

        button_settings_close.setOnClickListener {

            //TODO update values in  model

            finish()
        }


        // Repeted, refactor this later

        val adapterIcon = ArrayAdapter.createFromResource(
            this,
            R.array.settings_icons, android.R.layout.simple_spinner_item)

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
        spinner_settings_gender.adapter = adapterGender
        spinner_settings_looking_for.adapter = adapterLookingFor

        spinner_settings_icon.onItemSelectedListener = this
        spinner_settings_gender.onItemSelectedListener = this
        spinner_settings_looking_for.onItemSelectedListener = this

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
               name = s.toString()
            }
        })


    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, arg3: Long) {

        val value = parent.getItemAtPosition(position).toString()
        when (view.id){
            R.id.spinner_settings_icon -> {
                iconId = value.toInt()
            }
            R.id.spinner_settings_gender -> {
                gender = value
            }
            R.id.spinner_settings_looking_for -> {
                lookingFor = value
            }
            else -> {}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }
}