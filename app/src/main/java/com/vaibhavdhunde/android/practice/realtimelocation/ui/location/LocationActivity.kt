@file:Suppress("SpellCheckingInspection")

package com.vaibhavdhunde.android.practice.realtimelocation.ui.location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.model.MyLocation
import com.vaibhavdhunde.android.practice.realtimelocation.util.EventObserver
import com.vaibhavdhunde.android.practice.realtimelocation.util.hideSpotsDialog
import com.vaibhavdhunde.android.practice.realtimelocation.util.obtainViewModel
import com.vaibhavdhunde.android.practice.realtimelocation.util.showSpotsDialog
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import timber.log.Timber

class LocationActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {

    companion object {
        const val EXTRA_USER_ID =
            "com.vaibhavdhunde.android.practice.realtimelocation.extra.USER_ID"
        const val EXTRA_USER_NAME =
            "com.vaibhavdhunde.android.practice.realtimelocation.extra.USER_NAME"
        const val EXTRA_USER_EMAIL =
            "com.vaibhavdhunde.android.practice.realtimelocation.extra.USER_EMAIL"
    }

    private lateinit var mMap: GoogleMap

    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var email: String

    private lateinit var trackingUserLocation: DatabaseReference

    private lateinit var viewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = obtainViewModel(LocationViewModel::class.java)
        setupEvents()
        setupNavigation()

        uid = intent.getStringExtra(EXTRA_USER_ID)!!
        name = intent.getStringExtra(EXTRA_USER_NAME)!!
        email = intent.getStringExtra(EXTRA_USER_EMAIL)!!

        registerEventRealtime()
    }

    override fun onResume() {
        super.onResume()
        trackingUserLocation.addValueEventListener(this)
    }

    override fun onStop() {
        trackingUserLocation.removeEventListener(this)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        // Skin
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.my_uber_style))
    }

    private fun registerEventRealtime() {
        trackingUserLocation = FireDb.locationRef.child(uid)
        trackingUserLocation.addValueEventListener(this)
    }

    override fun onCancelled(e: DatabaseError) {
        Timber.e("Failed to load tracking location due to: ${e.message}")
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.value != null) {
            val location = dataSnapshot.getValue(MyLocation::class.java)

            // add marker
            val userMarker = LatLng(location!!.latitude, location.longitude)

            val markerOptions = MarkerOptions().position(userMarker)
                .title("$name\n$email")
                .snippet(location.timeStamp)

            mMap.addMarker(markerOptions)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 16f))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_unfriend -> {
                viewModel.showUnfriendAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupEvents() {
        viewModel.showProgress.observe(this, Observer { show ->
            if (show) {
                showSpotsDialog(R.string.progress_dialog_processing_request)
            } else {
                hideSpotsDialog()
            }
        })

        viewModel.showMessage.observe(this, EventObserver { message ->
            toast(message)
        })

        viewModel.showUnfriendAlert.observe(this, EventObserver {
            showUnfriendAlertDialog()
        })
    }

    private fun setupNavigation() {
        viewModel.navigateBackToMainActivity.observe(this, EventObserver {
            finish()
        })
    }

    private fun showUnfriendAlertDialog() {
        val title = getString(R.string.dialog_title_unfriend)
        val message = "Do you want to unfriend $name ($email)"
        alert(message, title) {
            setTheme(R.style.DialogTheme)
            noButton { it.dismiss() }
            positiveButton(getString(R.string.action_unfriend)) {
                viewModel.unfriend(uid)
            }
        }.show()
    }
}
