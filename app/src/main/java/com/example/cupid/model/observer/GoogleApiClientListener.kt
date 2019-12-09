package com.example.cupid.model.observer

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

interface GoogleApiClientListener :
    GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {

    override fun onConnected(p0: Bundle?)
    override fun onConnectionSuspended(reason: Int)
    override fun onConnectionFailed(p0: ConnectionResult)
}