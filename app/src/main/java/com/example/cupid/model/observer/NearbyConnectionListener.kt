package com.example.cupid.model.observer

import com.example.cupid.controller.ConnectionService
import com.google.android.gms.nearby.connection.ConnectionInfo

interface NearbyConnectionListener {
    fun onConnectionInitiated(
        endpoint: ConnectionService.Endpoint?,
        connectionInfo: ConnectionInfo?
    )

    fun onConnectionFailed(endpoint: ConnectionService.Endpoint?)
}