package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer

class LoginController(private val model: DataAccessLayer) {

    private lateinit var view: LoginView

    fun bind(loginView: LoginView) {
        view = loginView
    }

    fun onLoginButtonClicked() {
        model.performLogin(view.getUsername(), view.getPassword())
    }

}