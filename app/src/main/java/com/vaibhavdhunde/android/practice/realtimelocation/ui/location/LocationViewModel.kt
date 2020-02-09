@file:Suppress("SpellCheckingInspection")

package com.vaibhavdhunde.android.practice.realtimelocation.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.Status
import com.vaibhavdhunde.android.practice.realtimelocation.util.Event

class LocationViewModel : ViewModel() {

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _showMessage = MutableLiveData<Event<Int>>()
    val showMessage: LiveData<Event<Int>> = _showMessage

    private val _navigateBackToMainActivity = MutableLiveData<Event<Unit>>()
    val navigateBackToMainActivity: LiveData<Event<Unit>> = _navigateBackToMainActivity

    private val _showUnfriendAlert = MutableLiveData<Event<Unit>>()
    val showUnfriendAlert: LiveData<Event<Unit>> = _showUnfriendAlert

    fun showUnfriendAlert() {
        _showUnfriendAlert.postValue(Event(Unit))
    }

    fun unfriend(uid: String) {
        _showProgress.postValue(true)
        FireDb.unfriend(uid) { status ->
            _showProgress.postValue(false)
            if (status == Status.SUCCESS) {
                _navigateBackToMainActivity.postValue(Event(Unit))
            } else {
                _showMessage.postValue(Event(R.string.error_unfriend))
            }
        }
    }
}