package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.view.LoginView

class LoginController(private val model: DataAccessLayer) {

    private lateinit var view: LoginView

    fun bind(loginView: LoginView) {
        view = loginView
    }

    fun onLoginButtonClicked() {
        model.performLogin(
            view.getName(),
            view.getAge(),
            view.getAvartarrId(),
            view.getPhotoPath(),
            view.getBio(),
            view.getMaccAddress()
        )
    }
}