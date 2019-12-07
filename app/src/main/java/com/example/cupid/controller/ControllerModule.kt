package com.example.cupid.controller

import com.example.cupid.model.ModelModule

object ControllerModule {
    fun loginContrroller() = LoginController(ModelModule.dataAccessLayer)
    fun nearbyController() = NearbyController(ModelModule.dataAccessLayer)
}