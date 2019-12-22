package com.example.cupid.model.observer

import com.example.cupid.model.domain.Endpoint
import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.view.MyConnectionService

interface NearbyPayloadListener {
    val mReceivingCondition : (NearbyPayload) -> Boolean

    // alert the listener that a new payload has arrived
    fun newPayloadReceived()
}