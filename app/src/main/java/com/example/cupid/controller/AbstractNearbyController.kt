package com.example.cupid.controller

import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.observer.NearbyPayloadListener
import com.example.cupid.view.MyConnectionService
import java.util.concurrent.locks.ReentrantLock

abstract class AbstractNearbyController : NearbyPayloadListener {
    // when accessing the internal state, this lock should be acquired
    protected val mLock = ReentrantLock()

    // the unique instance of 'MyConnectionService' will be lateinited
    // in the child class
    protected abstract val mConnectionService: MyConnectionService

    // Called when the other party has called 'rejectTheConnection'
    // or simply disconnected
    protected abstract fun connectionRejected()


    protected abstract fun processNearbyPayload(nearbyPayload: NearbyPayload)

    // Only called when also the other party agrees to proceed, thus 'protected'
    protected abstract fun proceedToNextStage()

    // Send a rejection message and go back to the MainActivity
    abstract fun rejectTheConnection()

    // Called only from the viewer; wait for the other party's cue
    abstract fun waitForProceeding()

}