package com.vaibhavdhunde.android.practice.realtimelocation.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.ui.main.MainActivity
import com.vaibhavdhunde.android.practice.realtimelocation.util.*
import org.jetbrains.anko.*

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = obtainViewModel(AuthViewModel::class.java)

        setupEvents()
        setupNavigation()
    }

    private fun setupEvents() {
        viewModel.askPermissions.observe(this, EventObserver {
            askPermissions()
        })

        viewModel.showProgress.observe(this, Observer { show ->
            if (show) {
                showSpotsDialog(R.string.progress_dialog_setup_acc)
            } else {
                hideSpotsDialog()
            }
        })

        viewModel.showMessage.observe(this, EventObserver { message ->
            toast(message)
            finish()
        })
    }

    private fun setupNavigation() {
        viewModel.authenticateUser.observe(this, EventObserver {
            authenticateUser()
        })

        viewModel.navigateToMainActivity.observe(this, EventObserver {
            startActivity(intentFor<MainActivity>().newTask().clearTask())
        })
    }

    private fun askPermissions() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        viewModel.checkUserLogin()
                    } else {
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showPermissionsDialog()
                        } else {
                            toast(R.string.permissions_denied)
                            finish()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun authenticateUser() {
        AuthUI.getInstance().createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher)
            .setAvailableProviders(getSignInProviders())
            .build().also {
                startActivityForResult(it, RC_AUTH)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.initUser()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val response = IdpResponse.fromResultIntent(data)
                if (response == null) {
                    finish()
                } else {
                    viewModel.handleErrors(response)
                }
            }
        }
    }

    private fun getSignInProviders(): List<AuthUI.IdpConfig> {
        return listOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(true)
                .build()
        )
    }

    private fun showPermissionsDialog() {
        alert(
            getString(R.string.dialog_permission_message),
            getString(R.string.dialog_permission_title)
        ) {
            positiveButton(R.string.action_go_to_settings) {
                openSettings()
                finish()
            }
            negativeButton(R.string.action_cancel) {
                it.dismiss()
                toast(R.string.permissions_denied)
                finish()
            }
        }.show()
    }

    private fun openSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }.also {
            startActivityForResult(it, RC_PERMISSIONS)
        }
    }

    companion object {
        private const val RC_AUTH = 100
        private const val RC_PERMISSIONS = 101
    }
}
