package com.example.cupid.controller

import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.domain.ReplyToken
import com.example.cupid.model.observer.QueueObserver

interface NearbyController : QueueObserver {
    fun processReplyToken(replyToken : ReplyToken)
    fun proceedToNextStage()
    fun rejectTheConnection()
}