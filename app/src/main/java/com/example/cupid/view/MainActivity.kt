package com.example.cupid.view


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.cupid.R
import com.example.cupid.controller.MainController
import com.example.cupid.model.ModelModule
import com.example.cupid.model.observer.*
import com.example.cupid.view.utils.getAvatarFromId
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.connection.Strategy.P2P_POINT_TO_POINT
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_discovered.*
import kotlinx.android.synthetic.main.dialog_waiting.*
import kotlinx.android.synthetic.main.drawer_navigation_header.view.*


class MainActivity() :
    AppCompatActivity(),
    MainView
{

    private val model = ModelModule.dataAccessLayer
    private val controller = MainController(model)

    private val MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      
        checkPermissions()

        controller.registerClient(
            Nearby.getConnectionsClient(this)
        )

        controller.bind(this)
        controller.init()

        val drawerLayout: DrawerLayout = drawer_layout

        main_button_menu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        main_button_debug.setOnClickListener {
            launchDiscoveredPopup(2, "Bob")
        }

        nav_menu.menu.findItem(R.id.nav_settings)
            .setOnMenuItemClickListener {
                val myIntent = Intent(this, SettingsActivity::class.java)
                this.startActivity(myIntent)
                true
            }

        nav_menu.menu.findItem(R.id.nav_about)
            .setOnMenuItemClickListener {
                val myIntent = Intent(this, AboutActivity::class.java)
                this.startActivity(myIntent)
                true
            }

        main_button_discover.setOnClickListener {
            controller.hitDiscoverButton()
        }
    }
      
    override fun onStart() {
        super.onStart()
        controller.updateUserInfo()
    }

    override fun onStop() {
        super.onStop()
        controller.stopAdvertising()
    }


    override fun updateUserInfo(avatarId: Int, name: String) {
        nav_menu.getHeaderView(0)
            .layout_drawer_navigation_header
            .imageView
            .setImageResource(
                getAvatarFromId(this, avatarId)
            )
        nav_menu
            .getHeaderView(0)
            .layout_drawer_navigation_header
            .textView.text = name
        main_button_menu.setImageResource(getAvatarFromId(this, avatarId))
    }

    override fun updateGradientAnimation() {
        
      
        val backAnimation = main_layout.background as AnimationDrawable
        backAnimation.setEnterFadeDuration(10)
        backAnimation.setExitFadeDuration(3000)
        backAnimation.start()
    }

    override fun updateClickListeners(mDiscovering: Boolean) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.stripe_anim)

        if (mDiscovering) {
            findViewById<Button>(R.id.main_button_discover).setText(R.string.button_discover_active)
            stripe_layout.startAnimation(anim)
            findViewById<ConstraintLayout>(R.id.main_layout).setBackgroundResource(R.drawable.gradient_animation_active)

            controller.startDiscovering()
        } else {
            findViewById<Button>(R.id.main_button_discover).setText(R.string.button_discover_inactive)
            stripe_layout.clearAnimation()
            findViewById<ConstraintLayout>(R.id.main_layout).setBackgroundResource(R.drawable.gradient_animation)

            controller.stopDiscovering()
        }
        updateGradientAnimation()
    }

    override fun launchDiscoveredPopup(
        partnerAvatarId: Int,
        partnerName: String
    ) {
        with(Dialog(this)) {
            setContentView(R.layout.dialog_discovered)

            image_discover_avatar
                .setImageResource(getAvatarFromId(this@MainActivity, partnerAvatarId))

            text_discover_name.text = partnerName

            button_discover_connect.setOnClickListener {
                // Agreement to go further
                /*TODO check if partner has already answered */
                launchWaitingPopup()
                this.dismiss()
            }

            button_discover_close.setOnClickListener {
                this.dismiss()
            }

            window!!.attributes.windowAnimations = R.style.DialogAnimation
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    override fun launchWaitingPopup() {
        with(Dialog(this)) {
            setContentView(R.layout.dialog_waiting)
            button_waiting_close.setOnClickListener {
                this.dismiss()
                /* TODO normally just dismiss, this is for testing purposes*/
                val myIntent = Intent(
                    this@MainActivity,
                    QuizQuestionsActivity::class.java
                )
                //myIntent.putExtra("key", value) //Optional parameters
                super.startActivity(myIntent)
                /**/
            }
            window!!.attributes.windowAnimations = R.style.DialogAnimation
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }


    override fun checkPermissions(): Boolean {
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
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                arrNeededPermissions.add(permission)
            }
        }

        return if (arrNeededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                arrNeededPermissions.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            false
        } else {
            // Permission has already been granted
            true
        }

        /* https://developer.android.com/training/permissions/requesting END */
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty()) {
                    var permissionsDenied = ""
                    for (i in 0 until permissions.size) {
                        val permission = permissions[i]
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
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

    override fun partnerFound(avatarId: Int, name: String) {
        launchDiscoveredPopup(avatarId, name)
    }
}
