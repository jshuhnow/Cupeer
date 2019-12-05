package com.example.cupid.view.data

data class ResultUI (
    val question_text: String = "",
    val answerYou : String,
    val answerPartner : String,
    val nameYou : String,
    val namePartner : String
    //val iconYou : ImageView = view.image_result_you
    //val iconPartner : ImageView = view.image_result_partner
)