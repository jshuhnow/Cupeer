package com.example.cupid.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import com.example.cupid.model.observer.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import java.util.*


/** A class that connects to Nearby Connections and provides convenience methods and callbacks.  */
class MyConnectionService : GoogleApiClientListener {
    private var mGoogleApiClientListener: GoogleApiClientListener? = null
    private var mNearbyAdvertisingListener: NearbyAdvertisingListener? = null
    private val mNearbyDiscoveringListener: NearbyDiscoveringListener? = null
    private val mNearbyDataListener: NearbyDataListener? = null
    private val mNearbyEndpointListener: NearbyEndpointListener? = null
    private val mNearbyConnectionListener: NearbyConnectionListener? = null

    fun getConnectionsClient() = mConnectionsClient
    fun setConnectionsClient(connectionsClient: ConnectionsClient) {
        mConnectionsClient = connectionsClient
    }

    companion object {
        /**
         * An optional hook to pool any permissions the app needs with the permissions ConnectionsActivity
         * will request.
         *
         * @return All permissions required for the app to properly function.
         */
        /**
         * These permissions are required before connecting to Nearby Connections. Only [ ][Manifest.permission.ACCESS_COARSE_LOCATION] is considered dangerous, so the others should be
         * granted just by having them in our AndroidManfiest.xml
         */

        private val TAG = "ConnectionsActivity"
        private val ourInstance = MyConnectionService()

        fun getInstance(): MyConnectionService {
            return ourInstance
        }

        private var mConnectionsClient : ConnectionsClient? = null
    }


    //Check out ConnectionActivity.java class in google sample code and write similar methods
    //and also call the respective methods of the listeners.
    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "CONNECTED")
        mGoogleApiClientListener!!.onConnected(p0)

    }

    override fun onConnectionSuspended(reason: Int) {
        Log.d(TAG, "ConnectionSuspended")
        mGoogleApiClientListener!!.onConnectionSuspended(reason)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "ConnectionResult")
        mGoogleApiClientListener!!.onConnectionFailed(p0)
    }
}
