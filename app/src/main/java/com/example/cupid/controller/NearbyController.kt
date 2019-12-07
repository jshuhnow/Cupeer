package com.example.cupid.controller

import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.view.MainActivity
import com.example.cupid.view.NearbyView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.Nearby.Messages
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Strategy

class NearbyController (private val model: DataAccessLayer, context : Context){
    private lateinit var view: NearbyView
    private var mResolvingError = false

    companion object {
        val TAG = "MainActivity"
        val NAMESPACE = "nearby-controller"
    }

    fun bind(nearbyView : NearbyView) {
        this.view = nearbyView
    }

    // Listener on message received
    val messageListener = object : MessageListener() {
        override fun onFound(message: Message?) {
            Log.d(TAG, "onFound: ${message?.toString()}")
            if (message != null) {
                // message.getContent()
            }
        }
    }

    val googleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(context)
            .addApi(Nearby.MESSAGES_API)
            .addConnectionCallbacks(NearbyConnectionCallbacks())
            .addOnConnectionFailedListener(NearbyConnectionFailedListener())
            .build()
    }

    val strategy = Strategy.Builder()
        .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
        .setDistanceType(Strategy.DISCOVERY_MODE_DEFAULT)
        .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
        .build()

    fun onStart() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect()
        }
    }
    fun onStop() {
        if (googleApiClient.isConnected()) {
            Messages.unsubscribe(googleApiClient, messageListener)
        }
        googleApiClient.disconnect()
    }

    // Callbacks for Nearby Connection
    private inner class NearbyConnectionCallbacks : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(connectionHint: Bundle?) {
            Log.d(TAG, "onConnected: $connectionHint")
            Messages.getPermissionStatus(googleApiClient)
                .setResultCallback(NearbyResultCallback("getPermissionStatus") {
                    Log.d(TAG, "Start subscribe")
                    Messages.subscribe(googleApiClient, messageListener)
                })
        }

        override fun onConnectionSuspended(p0: Int) {

        }
    }


    // Message sending/receiving callback
    private inner class NearbyResultCallback(
        val method: String, val runOnSuccess: () -> Unit) : ResultCallback<Status> {

        override fun onResult(status: Status) {
            if (status.isSuccess()) {
                Log.d(TAG, "$method succeeded.")
                runOnSuccess()
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    if (!mResolvingError) {
                        try {
                            // TODO
                            mResolvingError = true
                        } catch (e: IntentSender.SendIntentException) {
                            Log.d("Error", "$method failed with exception: " + e)
                        }
                    } else {
                        // This will be encountered on initial startup because we do
                        // both publish and subscribe together.  So having a toast while
                        // resolving dialog is in progress is confusing, so just log it.
                        Log.d(TAG, "$method failed with status: $status while resolving error.")
                    }
                } else {
                    Log.d(TAG, "$method failed with: $status resolving error: $mResolvingError")
                }
            }
        }
    }

    // Listener when Nearby Connection failed
    private class NearbyConnectionFailedListener : GoogleApiClient.OnConnectionFailedListener {
        override fun onConnectionFailed(p0: ConnectionResult) {
            Log.d(TAG, "onConnectionFailed")
        }
    }
}