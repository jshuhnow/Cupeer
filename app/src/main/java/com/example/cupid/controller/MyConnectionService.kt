package com.example.cupid.view

import android.os.Bundle
import android.os.Handler

import android.os.Parcelable
import android.util.Log
import androidx.core.os.postDelayed
import com.example.cupid.controller.util.NearbyRecvPayloadQueue
import com.example.cupid.controller.util.ParcelableUtil
import com.example.cupid.model.domain.*
import com.example.cupid.model.observer.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


/** A class that connects to Nearby Connections and provides convenience methods and callbacks.  */
class MyConnectionService
    : NearbyAdvertisingListener, NearbyDiscoveringListener
{
    private val mNearbyRecvPayloadQueue = NearbyRecvPayloadQueue()
    private var mEndPoint: Endpoint? = null
    private var mNearbyPayloadListener : NearbyPayloadListener? = null
    private val mLock = ReentrantLock()
    private var mNearbyEndpointListener : NearbyEndpointListener? = null

    private var mIsSearching = AtomicBoolean(false)

    fun searching() {
        val r = Random()
        var p = 1
        Handler().apply {
            val runnable = object : Runnable {
                override fun run() {
                    if (mIsSearching.get()) {
                        if (p == 1) startAdvertising()
                        else startDiscovering()
                        p = -p
                    }
                    postDelayed(this, (r.nextInt(5000) + 5000).toLong())
                }
            }
            postDelayed(runnable, (r.nextInt(5000) + 5000).toLong())
        }
    }


    fun getIsLocked() : Boolean {
        return mLock.isLocked
    }

    fun registerNearbyPayloadListener(nearbyPayloadListener: NearbyPayloadListener) {
        mNearbyPayloadListener = nearbyPayloadListener

    }

    fun releaseNearbyPayloadListener() {
        mNearbyPayloadListener = null
    }

    fun acquireLock() {
        assert(!mLock.isLocked)
        mLock.lock()
    }

    fun releaseLock() {
        assert(mLock.isLocked)
        mLock.unlock()
    }

    fun getConnectionsClient() = mConnectionsClient
    fun setConnectionsClient(connectionsClient: ConnectionsClient) {
        mConnectionsClient = connectionsClient
    }

    fun setNearbyEndpointListener(nearbyEndpointListener: NearbyEndpointListener) {
        mNearbyEndpointListener = nearbyEndpointListener
    }

    fun send(obj: Parcelable) {
        val type = when (obj) {
            is Account -> "Account"
            is Answer -> "Answer"
            is Message -> "Message"
            is ReplyToken -> "ReplyToken"
            else -> "Error"
        }
        val payload = Payload.fromBytes(
            ParcelableUtil.marshall(NearbyPayload(type, obj))
        )

        send(payload, mEstablishedConnections.keys)
    }

    fun startSearching() {
        mIsSearching.set(true)
    }

    fun stopSearching() {
        mIsSearching.set(false)
    }

    fun conditionalPull() : NearbyPayload? {
        mLock.lock()
        val res = mNearbyPayloadListener?.let { mNearbyRecvPayloadQueue.conditionalPull(it) }
        mLock.unlock()
        return res
    }

    companion object {
        private val ourInstance = MyConnectionService()

        private val TAG = "ConnectionsActivity"
        private val STRATEGY = Strategy.P2P_POINT_TO_POINT
        private val SERVICE_ID = "123456"
        private val NAME = "Cupid"

        fun getInstance(): MyConnectionService {
            return ourInstance
        }

        private var mConnectionsClient: ConnectionsClient? = null
    }

    /** The devices we've discovered near us.  */
    private val mDiscoveredEndpoints: MutableMap<String, Endpoint> =
        HashMap()

    /**
     * The devices we have pending connections to. They will stay pending until we call [ ][.acceptConnection] or [.rejectConnection].
     */
    private val mPendingConnections: MutableMap<String, Endpoint> =
        HashMap()

    /**
     * The devices we are currently connected to. For advertisers, this may be large. For discoverers,
     * there will only be one entry in this map.
     */
    private val mEstablishedConnections: MutableMap<String, Endpoint?> =
        HashMap()

    /**
     * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
     * device.
     */
    private var mIsConnecting = false

    /** True if we are discovering.  */
    private var mIsDiscovering = false

    /** True if we are advertising.  */
    private var mIsAdvertising = false

    /** Callbacks for connections to other devices.  */
    private val mConnectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {
                logD(
                    String.format(
                        "onConnectionInitiated(endpointId=%s, endpointName=%s)",
                        endpointId, connectionInfo.endpointName
                    )
                )
                val endpoint = Endpoint(endpointId, connectionInfo.endpointName)
                mPendingConnections[endpointId] = endpoint

                // Accept
                acceptConnection(endpoint)
            }

            override fun onConnectionResult(
                endpointId: String,
                result: ConnectionResolution
            ) {
                logD(
                    String.format(
                        "onConnectionResponse(endpointId=%s, result=%s)",
                        endpointId,
                        result
                    )
                )
                // We're no longer connecting
                mIsConnecting = false


                if (!result.status.isSuccess) {
                    logW(
                        java.lang.String.format(
                            "Connection failed. Received status %s.",
                            toString(result.status)
                        )
                    )
                    onConnectionFailed(mPendingConnections.remove(endpointId))
                    return
                }
                connectedToEndpoint(mPendingConnections.remove(endpointId))
            }

            override fun onDisconnected(endpointId: String) {
                if (!mEstablishedConnections.containsKey(endpointId)) {
                    logW("Unexpected disconnection from endpoint $endpointId")
                    return
                }
                disconnectedFromEndpoint(mEstablishedConnections[endpointId])
            }
        }

    /** Callbacks for payloads (bytes of data) sent from another device to us.  */
    private val mPayloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            logD(
                String.format(
                    "onPayloadReceived(endpointId=%s, payload=%s)",
                    endpointId,
                    payload
                )
            )
            onReceive(mEstablishedConnections[endpointId], payload)
        }

        override fun onPayloadTransferUpdate(
            endpointId: String,
            update: PayloadTransferUpdate
        ) {
            logD(
                String.format(
                    "onPayloadTransferUpdate(endpointId=%s, update=%s)", endpointId, update
                )
            )
        }
    }

    /**
     * Sets the device to advertising mode. It will broadcast to other devices in discovery mode.
     * Either [.onAdvertisingStarted] or [.onAdvertisingFailed] will be called once
     * we've found out if we successfully entered this mode.
     */
    private fun startAdvertising() {
        if (isDiscovering()) stopDiscovering()
        if (isAdvertising()) return

        mIsAdvertising = true
        val localEndpointName = NAME
        val advertisingOptions = AdvertisingOptions.Builder()
        advertisingOptions.setStrategy(STRATEGY)
        mConnectionsClient!!
            .startAdvertising(
                localEndpointName,
                SERVICE_ID,
                mConnectionLifecycleCallback,
                advertisingOptions.build()
            )
            .addOnSuccessListener {
                logV("Now advertising endpoint $localEndpointName")
                onAdvertisingStarted()
            }
            .addOnFailureListener { e ->
                mIsAdvertising = false
                logW("startAdvertising() failed.", e)
                onAdvertisingFailed()
            }
    }

    /** Stops advertising.  */
    private fun stopAdvertising() {
        mIsAdvertising = false
        mConnectionsClient!!.stopAdvertising()
    }

    /** Returns `true` if currently advertising.  */
    private fun isAdvertising(): Boolean {
        return mIsAdvertising
    }

    /** Called when advertising successfully starts. Override this method to act on the event.  */
    override fun onAdvertisingStarted() {}

    /** Called when advertising fails to start. Override this method to act on the event.  */
    override fun onAdvertisingFailed() {
        startAdvertising()
    }

    /**
     * Called when a pending connection with a remote endpoint is created. Use [ConnectionInfo]
     * for metadata about the connection (like incoming vs outgoing, or the authentication token). If
     * we want to continue with the connection, call [.acceptConnection]. Otherwise,
     * call [.rejectConnection].
     */
    private fun onConnectionInitiated(
        endpoint: Endpoint?,
        connectionInfo: ConnectionInfo?
    ) {
    }

    /** Accepts a connection request.  */
    private fun acceptConnection(endpoint: Endpoint) {
        mConnectionsClient!!
            .acceptConnection(endpoint.id, mPayloadCallback)
            .addOnFailureListener { e -> logW("acceptConnection() failed.", e) }
    }

    /** Rejects a connection request.  */
    private fun rejectConnection(endpoint: Endpoint) {
        mConnectionsClient!!
            .rejectConnection(endpoint.id)
            .addOnFailureListener { e -> logW("rejectConnection() failed.", e) }
    }

    /**
     * Sets the device to discovery mode. It will now listen for devices in advertising mode. Either
     * [.onDiscoveryStarted] or [.onDiscoveryFailed] will be called once we've found
     * out if we successfully entered this mode.
     */
    private fun startDiscovering() {
        if (isAdvertising()) stopAdvertising()
        if (isDiscovering()) return

        mIsDiscovering = true
        mDiscoveredEndpoints.clear()
        val discoveryOptions = DiscoveryOptions.Builder()
        discoveryOptions.setStrategy(STRATEGY)

        mConnectionsClient!!
            .startDiscovery(
                SERVICE_ID,
                object : EndpointDiscoveryCallback() {
                    override fun onEndpointFound(
                        endpointId: String,
                        info: DiscoveredEndpointInfo
                    ) {
                        logD(
                            String.format(
                                "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                                endpointId, info.serviceId, info.endpointName
                            )
                        )
                        if (SERVICE_ID == info.serviceId) {
                            val endpoint = Endpoint(endpointId, info.endpointName)
                            mDiscoveredEndpoints[endpointId] = endpoint
                            onEndpointDiscovered(endpoint)
                        }
                    }

                    override fun onEndpointLost(endpointId: String) {
                        logD(String.format("onEndpointLost(endpointId=%s)", endpointId))
                    }
                },
                discoveryOptions.build()
            )
            .addOnSuccessListener { onDiscoveryStarted() }
            .addOnFailureListener { e ->
                mIsDiscovering = false
                logW("startDiscovering() failed.", e)
                onDiscoveryFailed()
            }
    }

    /** Stops discovery.  */
    private fun stopDiscovering() {
        mIsDiscovering = false
        mConnectionsClient!!.stopDiscovery()
    }

    /** Returns `true` if currently discovering.  */
    private fun isDiscovering(): Boolean {
        return mIsDiscovering
    }

    /** Called when discovery successfully starts. Override this method to act on the event.  */
    override fun onDiscoveryStarted() {}

    /** Called when discovery fails to start. Override this method to act on the event.  */
    override fun onDiscoveryFailed() {
        startDiscovering()
    }

    /**
     * Called when a remote endpoint is discovered. To connect to the device, call [ ][.connectToEndpoint].
     */
    private fun onEndpointDiscovered(endpoint: Endpoint) {
        connectToEndpoint(endpoint)
    }

    /** Disconnects from the given endpoint.  */
    private fun disconnect(endpoint: Endpoint) {
        mConnectionsClient!!.disconnectFromEndpoint(endpoint.id)
        mEstablishedConnections.remove(endpoint.id)
    }


    /** Disconnects from all currently connected endpoints.  */
    private fun disconnectFromAllEndpoints() {
        for (endpoint in mEstablishedConnections.values) {
            mConnectionsClient!!.disconnectFromEndpoint(endpoint!!.id)
        }
        mEstablishedConnections.clear()
    }

    /** Resets and clears all state in Nearby Connections.  */
    fun disconnect() {
        mLock.lock()

        mConnectionsClient!!.stopAllEndpoints()
        mIsAdvertising = false
        mIsDiscovering = false
        mIsConnecting = false
        mDiscoveredEndpoints.clear()
        mPendingConnections.clear()
        mEstablishedConnections.clear()

        mLock.unlock()
    }

    /**
     * Sends a connection request to the endpoint. Either [.onConnectionInitiated] or [.onConnectionFailed] will be called once we've found out
     * if we successfully reached the device.
     */
    private fun connectToEndpoint(endpoint: Endpoint) {
        logV("Sending a connection request to endpoint $endpoint")
        // Mark ourselves as connecting so we don't connect multiple times
        mIsConnecting = true
        // Ask to connect
        mConnectionsClient!!
            .requestConnection(NAME, endpoint.id, mConnectionLifecycleCallback)
            .addOnFailureListener { e ->
                logW("requestConnection() failed.", e)
                mIsConnecting = false
                onConnectionFailed(endpoint)
            }
    }

    /** Returns `true` if we're currently attempting to connect to another device.  */
    private fun isConnecting(): Boolean {
        return mIsConnecting
    }

    private fun connectedToEndpoint(endpoint: Endpoint?) {
        logD(String.format("connectedToEndpoint(endpoint=%s)", endpoint))
        mEstablishedConnections[endpoint!!.id] = endpoint
        onEndpointConnected(endpoint)
    }

    private fun disconnectedFromEndpoint(endpoint: Endpoint?) {
        logD(String.format("disconnectedFromEndpoint(endpoint=%s)", endpoint))
        mEstablishedConnections.remove(endpoint!!.id)
        onEndpointDisconnected(endpoint)
    }

    /**
     * Called when a connection with this endpoint has failed. Override this method to act on the
     * event.
     */
    private fun onConnectionFailed(endpoint: Endpoint?) {
        startSearching()
    }

    /** Called when someone has connected to us. Override this method to act on the event.  */
    private fun onEndpointConnected(endpoint: Endpoint) {
        mEndPoint = endpoint

        mLock.lock()

        if (mIsSearching.get()) {
            if (isDiscovering()) stopDiscovering()
            if (isAdvertising()) stopAdvertising()
            mNearbyEndpointListener?.onEndpointConnected()
        } else {
            disconnect(endpoint)
        }

        mLock.unlock()
    }

    /** Called when someone has disconnected. Override this method to act on the event.  */
    private fun onEndpointDisconnected(endpoint: Endpoint?) {
        mLock.lock()
        mNearbyRecvPayloadQueue.clear()
        mLock.unlock()

        mNearbyEndpointListener?.onEndpointDisconnected(endpoint)
    }

    /** Returns a list of currently connected endpoints.  */
    private fun getDiscoveredEndpoints(): Set<Endpoint?>? {
        return HashSet(mDiscoveredEndpoints.values)
    }

    /** Returns a list of currently connected endpoints.  */
    private fun getConnectedEndpoints(): Set<Endpoint?>? {
        return HashSet(mEstablishedConnections.values)
    }

    /**
     * Sends a [Payload] to all currently connected endpoints.
     *
     * @param payload The data you want to send.
     */



    private fun send(
        payload: Payload,
        endpoints: Set<String>
    ) {
        mConnectionsClient!!
            .sendPayload(ArrayList(endpoints), payload)
            .addOnFailureListener { e -> logW("sendPayload() failed.", e) }
    }

    /**
     * Someone connected to us has sent us data. Override this method to act on the event.
     *
     * @param endpoint The sender.
     * @param payload The data.
     */
    private fun onReceive(endpoint: Endpoint?, payload: Payload?) {
        logV(payload.toString())
        val bytes = payload!!.asBytes()
        val parcel = ParcelableUtil.unmarshall(bytes!!)
        val nearbyPayload = NearbyPayload(parcel)

        mLock.lock()
        mNearbyRecvPayloadQueue.enqueue(nearbyPayload)
        mNearbyPayloadListener?.let { it.newPayloadReceived() }
        mLock.unlock()
    }

    /**
     * Transforms a [Status] into a English-readable message for logging.
     *
     * @param status The current status
     * @return A readable String. eg. [404]File not found.
     */
    private fun toString(status: Status): String? {
        return java.lang.String.format(
            Locale.US,
            "[%d]%s",
            status.getStatusCode(),
            if (status.getStatusMessage() != null) status.getStatusMessage() else ConnectionsStatusCodes.getStatusCodeString(
                status.getStatusCode()
            )
        )
    }

    fun logV(msg: String) {
        Log.v(TAG, msg)
    }

    fun logD(msg: String) {
        Log.d(TAG, msg)
    }

    fun logW(msg: String) {
        Log.w(TAG, msg)
    }

    fun logW(msg: String, e: Throwable?) {
        Log.w(TAG, msg, e)
    }

    fun logE(msg: String, e: Throwable?) {
        Log.e(TAG, msg, e)
    }
}

