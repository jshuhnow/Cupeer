package com.example.cupid.model.observer

import com.example.cupid.controller.ConnectionService
import com.google.android.gms.nearby.connection.Payload

interface NearbyDataListener {
    fun onReceive(endpoint: ConnectionService.Endpoint?, payload: Payload?)
}