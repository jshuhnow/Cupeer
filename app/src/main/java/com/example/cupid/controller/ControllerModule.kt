package com.example.cupid.controller

import com.example.cupid.model.ModelModule

object ControllerModule {
    fun mainController() = MainController(ModelModule.dataAccessLayer)

}