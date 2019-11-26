package com.example.cupid.model

object ModelModule {

    val dataAccessLayer : DataAccessLayer by lazy { dataAccessLayer() }

    private fun dataAccessLayer() = DataAccessLayer()
}