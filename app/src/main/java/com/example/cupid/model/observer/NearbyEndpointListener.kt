package com.example.cupid.model.observer

import com.example.cupid.view.MyConnectionService

interface NearbyEndpointListener {
    //fun onEndpointDiscovered(endpoint: MyConnectionService?)
    fun onEndpointConnected()
    //fun onEndpointDisconnected(endpoint: ConnectionService.Endpoint?)
}