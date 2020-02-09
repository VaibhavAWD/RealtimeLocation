package com.vaibhavdhunde.android.practice.realtimelocation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireAuth
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.model.User
import com.vaibhavdhunde.android.practice.realtimelocation.util.Event

class MainViewModel : ViewModel() {

    private val _friends = MutableLiveData<List<User>?>()
    val friends: LiveData<List<User>?> = _friends

    private val _authenticateUser = MutableLiveData<Event<Unit>>()
    val authenticateUser: LiveData<Event<Unit>> = _authenticateUser

    private val _navigateToExploreActivity = MutableLiveData<Event<Unit>>()
    val navigateToExploreActivity: LiveData<Event<Unit>> = _navigateToExploreActivity

    private val _navigateToRequestsActivity = MutableLiveData<Event<Unit>>()
    val navigateToRequestsActivity: LiveData<Event<Unit>> = _navigateToRequestsActivity

    private val _navigateToLocationActivity = MutableLiveData<Event<User>>()
    val navigateToLocationActivity: LiveData<Event<User>> = _navigateToLocationActivity

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _showMessage = MutableLiveData<Event<Int>>()
    val showMessage: LiveData<Event<Int>> = _showMessage

    private val isDataLoading = MutableLiveData<Boolean>()

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    init {
        _isDataAvailable.postValue(true)
        loadAllFriends()
    }

    private fun loadAllFriends() {
        isDataLoading.postValue(true)
        showProgress()
        FireDb.getAllFriends(::onDataLoaded)
    }

    private fun onDataLoaded(friends: List<User>?) {
        hideProgress()
        if (friends.isNullOrEmpty()) {
            onDataNotAvailable()
            return
        }
        _friends.postValue(friends)
        isDataLoading.postValue(false)
        _isDataAvailable.postValue(true)
    }

    private fun onDataNotAvailable() {
        _friends.postValue(null)
        isDataLoading.postValue(false)
        _isDataAvailable.postValue(false)
    }

    fun reloadData() {
        isDataLoading.value?.let { isLoading ->
            if (isLoading) return
            loadAllFriends()
        }
    }

    fun navigateToExploreActivity() {
        _navigateToExploreActivity.postValue(Event(Unit))
    }

    fun navigateToRequestsActivity() {
        _navigateToRequestsActivity.postValue(Event(Unit))
    }

    fun logoutUser() {
        FireAuth.logoutUser()
        _authenticateUser.postValue(Event(Unit))
    }

    fun onClickFriend(user: User) {
        _navigateToLocationActivity.postValue(Event(user))
    }

    private fun showProgress() {
        _showProgress.postValue(true)
    }

    private fun hideProgress() {
        _showProgress.postValue(false)
    }

    @Suppress("unused")
    private fun showMessage(message: Int) {
        _showMessage.postValue(Event(message))
    }
}