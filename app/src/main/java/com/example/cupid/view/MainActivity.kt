package com.example.cupid.view


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cupid.R
import com.example.cupid.controller.ControllerModule.mainController
import com.example.cupid.model.ModelModule
import com.example.cupid.view.utils.getAvatarFromId
import com.example.cupid.view.utils.launchRejectedPopup
import com.example.cupid.view.utils.returnToMain
import com.example.cupid.view.views.MainView
import com.google.android.gms.nearby.Nearby
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_discovered.*
import kotlinx.android.synthetic.main.dialog_waiting.*
import kotlinx.android.synthetic.main.drawer_navigation_header.view.*
import kotlin.math.log

class MainActivity() :
    AppCompatActivity(),
    MainView
{

    private val model = ModelModule.dataAccessLayer
    private val controller = mainController()

    private val MULTIPLE_PERMISSIONS = 1

    private var mDiscoveredDialog : Dialog? = null
    private var mWaitingDialog : Dialog? = null
    private var mRejectedDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      
        checkPermissions()
        setListeners()

        controller.registerClient(
            Nearby.getConnectionsClient(this)
        )

        controller.bind(this)
        controller.init()

        controller.startBackgroundThreads()
    }
    private fun setListeners() {
        main_button_menu.setOnClickListener {
            val myIntent = Intent(this, SettingsActivity::class.java)
            this.startActivity(myIntent)
        }

        main_button_instruction.setOnClickListener {
            if(controller.isSearching()){
                controller.hitDiscoverButton()
            }
            controller.startInstructionDialog()

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
    override fun onResume() {
        super.onResume()
        controller.updateUserInfo()
        controller.registerNearbyPayloadListener()
        controller.reset()
    }

    override fun onPause() {
        super.onPause()
        controller.releaseNearbyPayloadListener()
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

    override fun updateClickListeners(isSearching: Boolean) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.stripe_anim)

        if (isSearching) {
            findViewById<Button>(R.id.main_button_discover).setText(R.string.button_discover_active)
            stripe_layout.startAnimation(anim)
            findViewById<ConstraintLayout>(R.id.main_layout).setBackgroundResource(R.drawable.gradient_animation_active)

        } else {
            findViewById<Button>(R.id.main_button_discover).setText(R.string.button_discover_inactive)
            stripe_layout.clearAnimation()
            findViewById<ConstraintLayout>(R.id.main_layout).setBackgroundResource(R.drawable.gradient_animation)
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
                controller.waitForProceeding()
                this.dismiss()
            }

            if(model.inInstructionMode()){
                this.setCancelable(false)
            }else{

                button_discover_close.setOnClickListener {
                    controller.rejectTheConnection()
                    this.dismiss()
                    returnToMain(this@MainActivity)
                }

                this.setOnCancelListener {
                    controller.rejectTheConnection()
                    this.dismiss()
                    returnToMain(this@MainActivity)
                }
            }

            window!!.attributes.windowAnimations = R.style.DialogAnimation
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()

            mDiscoveredDialog = this
        }
    }

    override fun launchWaitingPopup() {
        mWaitingDialog = Dialog(this)
        with(mWaitingDialog!!) {
            setContentView(R.layout.dialog_waiting)
            this.setCancelable(false)
            if(model.inInstructionMode()){

                Handler().postDelayed({
                    proceedToNextStage()

                }, 1500)

            }else{
                button_waiting_close.setOnClickListener {
                    controller.rejectTheConnection()
                    dismiss()
                    returnToMain(this@MainActivity)
                }
            }

            window!!.attributes.windowAnimations = R.style.DialogAnimation
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    override fun launchRejectedPopup() {
        mWaitingDialog?.dismiss()
        mRejectedDialog = launchRejectedPopup(this)
    }


    override fun proceedToNextStage() {
        if (mWaitingDialog != null)
            mWaitingDialog!!.dismiss()

        val DEV = false

        if (DEV) {
            val myIntent = Intent(
                this@MainActivity,
                ChatActivity::class.java
            )
            super.startActivity(myIntent)

        } else {
            val myIntent = Intent(
                this@MainActivity,
                QuizQuestionsActivity::class.java
            )
            super.startActivity(myIntent)
        }
    }

    override fun dismissPopups() {
        mDiscoveredDialog?.dismiss()
        mWaitingDialog?.dismiss()
        mRejectedDialog?.dismiss()
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

    override fun onBackPressed() {

    }
    override fun onDestroy() {
        super.onDestroy()
        dismissPopups()
    }


}
