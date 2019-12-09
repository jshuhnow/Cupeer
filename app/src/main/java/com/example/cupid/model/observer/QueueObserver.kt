package com.example.cupid.model.observer

import com.example.cupid.model.domain.NearbyPayload

interface QueueObserver {
    fun newElement(nearbyPayload: NearbyPayload)
}