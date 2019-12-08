package com.example.cupid.controller

import android.os.Bundle
import android.util.Log
import com.example.cupid.model.observer.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient


class ConnectionService : GoogleApiClientListener {
    //create reference for all the listeneres that you have created

    private var mGoogleApiClientListener: GoogleApiClientListener? = null
    private var mNearbyAdvertisingListener: NearbyAdvertisingListener? = null
    private val mNearbyDiscoveringListener: NearbyDiscoveringListener? = null
    private val mNearbyDataListener: NearbyDataListener? = null
    private val mNearbyEndpointListener: NearbyEndpointListener? = null
    private val mNearbyConnectionListener: NearbyConnectionListener? = null
    private var mGoogleApiClient : GoogleApiClient? = null

    companion object {
        val TAG = "ConnectionService"
        private val instance = ConnectionService()
        fun getInstance(): ConnectionService {
            return instance
        }
    }
    //create setters for all the listeners that you have created

    fun setGoogleApiClientListener(googleApiClientListener: GoogleApiClientListener?) {
        mGoogleApiClientListener = googleApiClientListener
    }

    fun setNearbyAdvertisingListener(nearbyAdvertisingListener: NearbyAdvertisingListener?) {
        mNearbyAdvertisingListener = nearbyAdvertisingListener
    }

    fun getGoogleApiClient() = mGoogleApiClient
    fun setGoogleApiClient(googleApiClient : GoogleApiClient) {
        mGoogleApiClient = googleApiClient
        mGoogleApiClient!!.registerConnectionCallbacks(this)
        mGoogleApiClient!!.registerConnectionFailedListener(this)
        mGoogleApiClient!!.connect()
    }

    // Create setter for every member variable

    //Check out ConnectionActivity.java class in google sample code and write similar methods
    //and also call the respective methods of the listeners.
    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "CONNECTED")
    }

    override fun onConnectionSuspended(reason: Int) {
        Log.d(TAG, "ConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "ConnectionResult")
    }

    data class Endpoint (
        val id : String,
        val name : String
    )

}