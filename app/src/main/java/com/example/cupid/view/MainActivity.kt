package com.example.cupid.view


import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.example.cupid.R
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cupid.model.ModelModule
import com.example.cupid.model.observer.DomainObserver
import kotlinx.android.synthetic.main.activity_main.*
import androidx.constraintlayout.widget.ConstraintLayout
import android.graphics.Color
import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import com.example.cupid.view.utils.getAvatarFromId
import kotlinx.android.synthetic.main.dialog_discovered.*
import kotlinx.android.synthetic.main.dialog_waiting.*
import kotlinx.android.synthetic.main.drawer_navigation_header.view.*


class MainActivity : AppCompatActivity(), DomainObserver {

    private val model = ModelModule.dataAccessLayer
    private var discovering = false
    private val MULTIPLE_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // TODO dummy data
        var avatarId = 2
        var name = "Steven"


        main_button_menu.setImageResource(getAvatarFromId(this, avatarId))
        nav_menu.getHeaderView(0).layout_drawer_navigation_header.imageView.setImageResource(
            getAvatarFromId(this, avatarId)
        )
        nav_menu.getHeaderView(0).layout_drawer_navigation_header.textView.text = name

        updateGradientAnimation()
        setClickListeners()
        checkPermissions()
    }


    private fun updateGradientAnimation(){
        val backAnimation = main_layout.background as AnimationDrawable
        backAnimation.setEnterFadeDuration(10)
        backAnimation.setExitFadeDuration(3000)
        backAnimation.start()
    }

    private fun setClickListeners(){

        val drawerLayout: DrawerLayout = drawer_layout
        val anim = AnimationUtils.loadAnimation(this, R.anim.stripe_anim)

        main_button_discover.setOnClickListener {

            // TODO start/stop discovery
            if(!discovering){
                findViewById<Button>(R.id.main_button_discover).setText(R.string.button_discover_active)
                stripe_layout.startAnimation(anim)
                findViewById<ConstraintLayout>(R.id.main_layout).setBackgroundResource(R.drawable.gradient_animation_active)

                /* Merge: Start discovery process*/

            }else{
                findViewById<Button>(R.id.main_button_discover).setText(R.string.button_discover_inactive)
                stripe_layout.clearAnimation()
                findViewById<ConstraintLayout>(R.id.main_layout).setBackgroundResource(R.drawable.gradient_animation)

                /* Merge: Stop discovery process*/
            }
            discovering = !discovering

            updateGradientAnimation()
        }


        main_button_menu.setOnClickListener{
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        main_button_debug.setOnClickListener{
            launchDiscoveredPopup(it)
        }

        nav_menu.menu.findItem(R.id.nav_settings).setOnMenuItemClickListener {
            val myIntent = Intent(this, SettingsActivity::class.java)
            this.startActivity(myIntent)
            true

        }


        nav_menu.menu.findItem(R.id.nav_about).setOnMenuItemClickListener {
            val myIntent = Intent(this, AboutActivity::class.java)
            this.startActivity(myIntent)
            true

        }



    }


    private fun launchDiscoveredPopup(v: View) {


        val discoverDialog = Dialog(this)
        discoverDialog.setContentView(R.layout.dialog_discovered)

        // TODO dummy data -> Fill in on connect
        val partnerAvatarId = 8
        val partnerName = "Bob"

        discoverDialog.image_discover_avatar.setImageResource(getAvatarFromId(this,partnerAvatarId))
        discoverDialog.text_discover_name.text = partnerName

        discoverDialog.button_discover_connect.setOnClickListener{
            // Agreement to go further
            /*TODO check if partner has already answered */

            launchWaitingPopup(it)
            discoverDialog.dismiss()
        }

        discoverDialog.button_discover_close.setOnClickListener{
            discoverDialog.dismiss()
        }


        discoverDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        discoverDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        discoverDialog.show()

    }

    private fun launchWaitingPopup(v: View){

        val waitingDialog = Dialog(this)
        waitingDialog.setContentView(R.layout.dialog_waiting)


        waitingDialog.button_waiting_close.setOnClickListener{
            waitingDialog.dismiss()

            /* TODO normally just dismiss, this is for testing purposes*/

            val myIntent = Intent(this, QuizQuestionsActivity::class.java)
            //myIntent.putExtra("key", value) //Optional parameters
            this.startActivity(myIntent)
            /**/
        }


        waitingDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        waitingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waitingDialog.show()
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
                    permission) != PackageManager.PERMISSION_GRANTED) {
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
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
}
