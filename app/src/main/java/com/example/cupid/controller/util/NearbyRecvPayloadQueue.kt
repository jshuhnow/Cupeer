package com.example.cupid.controller.util

import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.observer.NearbyPayloadListener
import java.util.*

class NearbyRecvPayloadQueue {
    private val mMessageQueue = ArrayDeque<NearbyPayload>()
    fun enqueue(nearbyPayload: NearbyPayload) {
        mMessageQueue.add(nearbyPayload)
    }

    fun conditionalPull(nearbyPayloadListener: NearbyPayloadListener) : NearbyPayload? {
        var res : NearbyPayload? = null

        val it: Iterator<NearbyPayload> = mMessageQueue.iterator()
        while(it.hasNext()) {
            val current = it.next()
            if (nearbyPayloadListener.mReceivingCondition(current)) {
                res = current

                mMessageQueue.remove(current)
                break
            }
        }

        return res
    }

    fun clear() {
        mMessageQueue.clear()
    }
}