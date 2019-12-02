package com.example.cupid.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cupid.model.ModelModule
import com.example.cupid.model.observer.DomainObserver

class MainActivity : AppCompatActivity(), DomainObserver {
    private val model = ModelModule.dataAccessLayer
    private val MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.cupid.R.layout.activity_main)

        checkPermissions()
    }
    override fun onStart() {
        super.onStart()
        model.register(this)
    }

    override fun onStop() {
        super.onStop()
        model.unregister(this)
    }


    private fun checkPermissions() : Boolean {
        /* https://developer.android.com/training/permissions/requesting */

        // Here, thisActivity is the current activity
        val arrPermissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val arrNeededPermissions = ArrayList<String>()

        for (permission in arrPermissions) {
            if (ContextCompat.checkSelfPermission(this,
                    permission)
            != PackageManager.PERMISSION_GRANTED) {
                arrNeededPermissions.add(permission)
            }
        }

        return if (arrNeededPermissions.isNotEmpty()) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrNeededPermissions.toArray() as Array<out String>,
                MULTIPLE_PERMISSIONS
            )
            false
        } else {
            // Permission has already been granted
            true
        }

        /* https://developer.android.com/training/permissions/requesting END */
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty()) {
                    var permissionsDenied = ""
                    for (permission in permissions) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + permission
                        }

                    }


                } else {
                    // One of the permission has not been granted
                }

                // TODO: update view

                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
