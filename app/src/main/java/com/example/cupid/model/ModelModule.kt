package com.example.cupid.model

import com.example.cupid.model.repository.AccountRepository

object ModelModule {

    val dataAccessLayer : DataAccessLayer by lazy { dataAccessLayer() }

    private fun dataAccessLayer() = DataAccessLayer(AccountRepository())
}