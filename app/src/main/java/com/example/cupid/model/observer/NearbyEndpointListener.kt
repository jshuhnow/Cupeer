package com.example.cupid.model.observer

import com.example.cupid.controller.ConnectionService

interface NearbyEndpointListener {
    fun onEndpointDiscovered(endpoint: ConnectionService.Endpoint?)
    fun onEndpointConnected(endpoint: ConnectionService.Endpoint?)
    fun onEndpointDisconnected(endpoint: ConnectionService.Endpoint?)
}