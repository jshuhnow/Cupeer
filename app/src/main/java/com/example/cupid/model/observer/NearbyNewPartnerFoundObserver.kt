package com.example.cupid.model.observer

import com.example.cupid.model.domain.Endpoint
import com.example.cupid.view.MyConnectionService

interface NearbyNewPartnerFoundObserver {
    fun newPartnerfound(endpoint: Endpoint)
}