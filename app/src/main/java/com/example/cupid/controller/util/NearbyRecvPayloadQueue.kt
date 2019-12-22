package com.example.cupid.controller.util

import com.example.cupid.model.domain.NearbyPayload
import java.util.*

class NearbyRecvPayloadQueue {
    private val mMessageQueue = ArrayDeque<NearbyPayload>()

    fun enqueue(nearbyPayload: NearbyPayload) {
        mMessageQueue.add(nearbyPayload)
    }

    fun conditionalDequeue(filter : (NearbyPayload) -> Boolean) : NearbyPayload? {
        val it = mMessageQueue.iterator()
        var res : NearbyPayload? = null
        while(it.hasNext()) {
            res = it.next()
            if ( !filter(res)  ) {
                res = null
            } else {
                break
            }
        }

        res?.let { mMessageQueue.remove(res) }
        return res
    }
}