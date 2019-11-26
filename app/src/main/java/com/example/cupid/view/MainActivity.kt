package com.example.cupid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cupid.R
import com.example.cupid.model.ModelModule
import com.example.cupid.model.observer.DomainObserver

class MainActivity : AppCompatActivity(), DomainObserver {
    private val model = ModelModule.dataAccessLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onStart() {
        super.onStart()
        model.register(this)
    }

    override fun onStop() {
        super.onStop()
        model.unregister(this)
    }

}
