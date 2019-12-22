package com.example.cupid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cupid.R
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setClickListeners()
    }

    private fun setClickListeners() {
        button_about_close.setOnClickListener {
            finish()
        }
    }
}
