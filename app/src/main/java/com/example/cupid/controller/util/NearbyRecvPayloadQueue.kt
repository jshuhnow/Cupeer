package com.example.cupid.controller.util

import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.observer.QueueObserver
import com.google.android.gms.nearby.connection.Payload
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class NearbyRecvPayloadQueue {
    private val mMessageQueue = ArrayDeque<NearbyPayload>()
    private var mObserverQueue = ArrayDeque<QueueObserver>()

    private val lock = ReentrantLock()

    fun enqueue(nearbyPayload: NearbyPayload) {
        lock.lock()
        if (mObserverQueue.isNotEmpty()) {
            mObserverQueue.poll().newElementArrived(nearbyPayload)
        } else {
            mMessageQueue.add(nearbyPayload)
        }
        lock.unlock()
    }

    fun dequeue(queueObserver: QueueObserver) : NearbyPayload? {
        var res : NearbyPayload? = null
        lock.lock()
        if (mMessageQueue.isEmpty()) {
            this.mObserverQueue.add(queueObserver)
            res = null
        } else {
            res = mMessageQueue.poll()
        }
        lock.unlock()
        return res
    }
}