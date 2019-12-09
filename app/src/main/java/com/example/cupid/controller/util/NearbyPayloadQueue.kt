package com.example.cupid.controller.util

import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.observer.QueueObserver
import java.util.concurrent.locks.ReentrantLock

private class Queue (){

    val items = mutableListOf<NearbyPayload>()

    fun isEmpty():Boolean = items.isEmpty()

    fun size():Int = items.count()

    override fun toString() = items.toString()

    fun enqueue(element:NearbyPayload){
        items.add(element)
    }

    fun dequeue():NearbyPayload?{
        if (this.isEmpty()){
            return null
        } else {
            return items.removeAt(0)
        }
    }

    fun peek():Any?{
        return items[0]
    }
}

class MyPayloadQueue {
    private val mMessageQueue = Queue()
    private var queueObserver : QueueObserver? = null
    private val lock = ReentrantLock()

    fun enqueue(nearbyPayload: NearbyPayload) {
        lock.lock()
        mMessageQueue.enqueue(nearbyPayload)
        lock.unlock()
    }

    fun dequeue(queueObserver: QueueObserver) : NearbyPayload? {
        lock.lock()
        if (mMessageQueue.isEmpty()) {
            this.queueObserver = queueObserver
            return null
        } else {
            return mMessageQueue.dequeue()
        }
        lock.unlock()
    }
}