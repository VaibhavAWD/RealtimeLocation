package com.vaibhavdhunde.android.practice.realtimelocation.ui.main

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.databinding.ActivityMainBinding
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireAuth
import com.vaibhavdhunde.android.practice.realtimelocation.service.MyLocationReceiver
import com.vaibhavdhunde.android.practice.realtimelocation.ui.explore.ExploreActivity
import com.vaibhavdhunde.android.practice.realtimelocation.ui.location.LocationActivity
import com.vaibhavdhunde.android.practice.realtimelocation.ui.requests.RequestsActivity
import com.vaibhavdhunde.android.practice.realtimelocation.util.EventObserver
import com.vaibhavdhunde.android.practice.realtimelocation.util.obtainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var friendsAdapter: FriendsListAdapter

    private lateinit var locationRequest: LocationRequest

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()

        setSupportActionBar(toolbar)
        setupDrawerNavigation()
        setupSwipeRefresh()
        setupListFriends()
        updateLocation()

        setupEvents()
        setupNavigation()
    }

    private fun setupDataBinding() {
        viewModel = obtainViewModel(MainViewModel::class.java)

        friendsAdapter = FriendsListAdapter(viewModel)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            viewmodel = this@MainActivity.viewModel
            adapter = friendsAdapter
            lifecycleOwner = this@MainActivity
        }
    }

    private fun setupDrawerNavigation() {
        // display user info in the header
        val header = nav_drawer.getHeaderView(0)
        val userName = header.findViewById<TextView>(R.id.user_name)
        userName.text = FireAuth.getCurrentUser()!!.displayName!!
        val userEmail = header.findViewById<TextView>(R.id.user_email)
        userEmail.text = FireAuth.getCurrentUser()!!.email!!

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_drawer.setNavigationItemSelectedListener(this)
    }

    private fun setupEvents() {
        binding.viewmodel?.showProgress?.observe(this, Observer { show ->
            refresh_friends.isRefreshing = show
        })

        binding.viewmodel?.showMessage?.observe(this, EventObserver { message ->
            toast(message)
        })

        binding.viewmodel?.friends?.observe(this, Observer { listFriends ->
            friendsAdapter.submitList(listFriends)
        })
    }

    private fun setupNavigation() {
        binding.viewmodel?.navigateToExploreActivity?.observe(this, EventObserver {
            startActivity<ExploreActivity>()
        })

        binding.viewmodel?.navigateToRequestsActivity?.observe(this, EventObserver {
            startActivity<RequestsActivity>()
        })

        binding.viewmodel?.navigateToLocationActivity?.observe(this, EventObserver {
            startActivity<LocationActivity>(
                LocationActivity.EXTRA_USER_ID to it.uid,
                LocationActivity.EXTRA_USER_NAME to it.name,
                LocationActivity.EXTRA_USER_EMAIL to it.email
            )
        })

        binding.viewmodel?.authenticateUser?.observe(this, EventObserver {
            finish()
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_explore -> {
                binding.viewmodel?.navigateToExploreActivity()
            }
            R.id.nav_requests -> {
                binding.viewmodel?.navigateToRequestsActivity()
            }
            R.id.nav_logout -> {
                binding.viewmodel?.logoutUser()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupListFriends() {
        list_friends.setHasFixedSize(true)
        list_friends.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
    }

    private fun setupSwipeRefresh() {
        refresh_friends.setOnRefreshListener {
            binding.viewmodel?.reloadData()
        }
    }

    private fun updateLocation() {
        locationRequest = LocationRequest()
        locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL
        locationRequest.maxWaitTime = MAX_WAIT_TIME
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MyLocationReceiver::class.java)
        intent.action = MyLocationReceiver.ACTION_UPDATE_LOCATION
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val SMALLEST_DISPLACEMENT: Float = 10f
        private const val UPDATE_INTERVAL: Long = 10 * 1000
        private const val FASTEST_UPDATE_INTERVAL: Long = UPDATE_INTERVAL / 2
        private const val MAX_WAIT_TIME: Long = UPDATE_INTERVAL * 3
    }
}
