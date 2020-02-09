package com.vaibhavdhunde.android.practice.realtimelocation.ui.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.Status
import com.vaibhavdhunde.android.practice.realtimelocation.model.User
import com.vaibhavdhunde.android.practice.realtimelocation.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestsViewModel : ViewModel() {

    private val _requests = MutableLiveData<List<User>?>()
    val requests: LiveData<List<User>?> = _requests

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _navigateBackToMainActivity = MutableLiveData<Event<Unit>>()
    val navigateBackToMainActivity: LiveData<Event<Unit>> = _navigateBackToMainActivity

    private val _showMessage = MutableLiveData<Event<Int>>()
    val showMessage: LiveData<Event<Int>> = _showMessage

    private val _showRefreshing = MutableLiveData<Boolean>()
    val showRefreshing: LiveData<Boolean> = _showRefreshing

    private val isDataLoading = MutableLiveData<Boolean>()

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    init {
        _isDataAvailable.postValue(true)
        loadAllFriendRequests()
    }

    private fun loadAllFriendRequests() {
        isDataLoading.postValue(true)
        _showRefreshing.postValue(true)
        FireDb.getAllFriendRequests(::onDataLoaded)
    }

    private fun onDataLoaded(requests: List<User>?) {
        _showRefreshing.postValue(false)
        if (requests.isNullOrEmpty()) {
            onDataNotAvailable()
            return
        }
        _requests.postValue(requests)
        isDataLoading.postValue(false)
        _isDataAvailable.postValue(true)
    }

    private fun onDataNotAvailable() {
        _requests.postValue(null)
        isDataLoading.postValue(false)
        _isDataAvailable.postValue(false)
    }

    fun onAcceptFriendRequest(user: User) = CoroutineScope(Dispatchers.Main).launch {
        FireDb.deleteRequest(user.uid)
        showProgress()
        val status = FireDb.addToAcceptList(user)
        hideProgress()
        if (status == Status.SUCCESS) {
            _navigateBackToMainActivity.postValue(Event(Unit))
        } else {
            _showMessage.postValue(Event(R.string.error_something_went_wrong))
        }
    }

    fun onDeclineFriendRequest(user: User) {
        FireDb.deleteRequest(user.uid)
        _navigateBackToMainActivity.postValue(Event(Unit))
    }

    fun reloadData() {
        isDataLoading.value?.let { isLoading ->
            if (isLoading) return
            loadAllFriendRequests()
        }
    }

    private fun showProgress() {
        _showProgress.postValue(true)
    }

    private fun hideProgress() {
        _showProgress.postValue(false)
    }
}