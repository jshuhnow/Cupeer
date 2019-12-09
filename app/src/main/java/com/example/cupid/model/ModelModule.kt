package com.example.cupid.model

import com.example.cupid.model.repository.AccountRepository
import com.example.cupid.model.repository.MessageRepository
import com.example.cupid.model.repository.QuestionRepository

object ModelModule {

    val dataAccessLayer : DataAccessLayer by lazy { dataAccessLayer() }

    private fun dataAccessLayer() = DataAccessLayer(
        AccountRepository(),
        QuestionRepository(),
        MessageRepository()
    )
}