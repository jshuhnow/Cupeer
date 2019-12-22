package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.view.views.SettingsView

class SettingsController(
    private val model : DataAccessLayer
) {
    private lateinit var view: SettingsView

    fun bind(settingsView: SettingsView) {
        view = settingsView
    }

    fun readUserInformation() : Pair<Int, String> {
        return Pair(model.getUserAccount()!!.avatarId, model.getUserAccount()!!.name)
    }

    fun writeUserInformation(avatarId : Int, name : String) {
        model.getUserAccount()!!.avatarId = avatarId
        model.getUserAccount()!!.name = name
    }
}