package com.example.cupid.controller

import com.example.cupid.model.ModelModule

object ControllerModule {
    fun chatController() = ChatController(ModelModule.dataAccessLayer)
    fun mainController() = MainController(ModelModule.dataAccessLayer)
    fun quizQuestionsController() = QuizQuestionsController(ModelModule.dataAccessLayer)
    fun quizResultsController() = QuizResultsController(ModelModule.dataAccessLayer)
    fun settingsController() = SettingsController(ModelModule.dataAccessLayer)
}