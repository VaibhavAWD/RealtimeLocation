package com.vaibhavdhunde.android.practice.realtimelocation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.data.PaperUtil
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireAuth
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authenticateUser = MutableLiveData<Event<Unit>>()
    val authenticateUser: LiveData<Event<Unit>> = _authenticateUser

    private val _navigateToMainActivity = MutableLiveData<Event<Unit>>()
    val navigateToMainActivity: LiveData<Event<Unit>> = _navigateToMainActivity

    private val _askPermissions = MutableLiveData<Event<Unit>>()
    val askPermissions: LiveData<Event<Unit>> = _askPermissions

    private val _showMessage = MutableLiveData<Event<Int>>()
    val showMessage: LiveData<Event<Int>> = _showMessage

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    init {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500L)
            _askPermissions.postValue(Event(Unit))
        }
    }

    fun checkUserLogin() {
        if (FireAuth.isUserLoggedIn()) {
            _navigateToMainActivity.postValue(Event(Unit))
        } else {
            _authenticateUser.postValue(Event(Unit))
        }
    }

    fun initUser() {
        showProgress()
        FireDb.initUserIfFirstTime { user ->
            hideProgress()
            if (user != null) {
                PaperUtil.setUserUid(user.uid)
                updateToken()
                _navigateToMainActivity.postValue(Event(Unit))
            } else {
                showMessage(R.string.error_setup_failure)
            }
        }
    }

    private fun updateToken() {
        CoroutineScope(Dispatchers.IO).launch {
            FireDb.updateToken()
        }
    }

    fun handleErrors(response: IdpResponse) {
        when (response.error!!.errorCode) {
            ErrorCodes.NO_NETWORK -> showMessage(R.string.error_no_network)
            ErrorCodes.UNKNOWN_ERROR -> showMessage(R.string.error_unknown)
        }
    }

    private fun showProgress() {
        _showProgress.postValue(true)
    }

    private fun hideProgress() {
        _showProgress.postValue(false)
    }

    private fun showMessage(message: Int) {
        _showMessage.postValue(Event(message))
    }

}