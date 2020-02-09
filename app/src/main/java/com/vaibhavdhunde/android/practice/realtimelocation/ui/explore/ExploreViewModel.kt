package com.vaibhavdhunde.android.practice.realtimelocation.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireAuth
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireDb
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FriendStatus
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.RequestStatus
import com.vaibhavdhunde.android.practice.realtimelocation.model.Request
import com.vaibhavdhunde.android.practice.realtimelocation.model.RequestData
import com.vaibhavdhunde.android.practice.realtimelocation.model.User
import com.vaibhavdhunde.android.practice.realtimelocation.remote.NetworkException
import com.vaibhavdhunde.android.practice.realtimelocation.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExploreViewModel(private val repo: RemoteRepo) : ViewModel() {

    private val _people = MutableLiveData<List<User>?>()
    val people: LiveData<List<User>?> = _people

    private val _showFriendRequestDialog = MutableLiveData<Event<User>>()
    val showFriendRequestDialog: LiveData<Event<User>> = _showFriendRequestDialog

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _showMessage = MutableLiveData<Event<String>>()
    val showMessage: LiveData<Event<String>> = _showMessage

    private val _showRefreshing = MutableLiveData<Boolean>()
    val showRefreshing: LiveData<Boolean> = _showRefreshing

    private val isDataLoading = MutableLiveData<Boolean>()

    init {
        loadAllPeople()
    }

    private fun loadAllPeople() {
        isDataLoading.postValue(true)
        _showRefreshing.postValue(true)
        FireDb.getAllPeople { listPeople ->
            isDataLoading.postValue(false)
            _showRefreshing.postValue(false)
            _people.postValue(listPeople)
        }
    }

    fun onClickUser(user: User) {
        _showFriendRequestDialog.postValue(Event(user))
    }

    fun checkRequestAlreadySent(user: User) {
        showProgress()
        FireDb.checkRequestAlreadySent(user.uid) { requestStatus ->
            when (requestStatus) {
                RequestStatus.NOT_SENT -> {
                    checkUserAlreadyFriend(user)
                }
                RequestStatus.SENT -> {
                    hideProgress()
                    showMessage("Friend request has already been sent!")
                }
                RequestStatus.FAILURE -> {
                    hideProgress()
                    showMessage("Something went wrong. Please try again later")
                }
            }
        }
    }

    private fun checkUserAlreadyFriend(user: User) {
        FireDb.checkUserAlreadyFriend(user.uid) { friendStatus ->
            when (friendStatus) {
                FriendStatus.NOT_A_FRIEND -> {
                    sendFriendRequest(user)
                }
                FriendStatus.ALREADY_A_FRIEND -> {
                    hideProgress()
                    showMessage("Already a Friend!")
                }
                else -> {
                    hideProgress()
                    showMessage("Something went wrong. Please try again later")
                }
            }
        }
    }

    private fun sendFriendRequest(user: User) = CoroutineScope(Dispatchers.Main).launch {
        if (user.tokens == null) {
            hideProgress()
            showMessage("User token not available")
            return@launch
        }

        try {
            val response = repo.sendFriendRequest(getRequest(user))
            if (response.success == 1) {
                hideProgress()
                showMessage("Friend Request Sent!")
            }
        } catch (e: ApiException) {
            hideProgress()
            showMessage(e.message!!)
        } catch (e: NetworkException) {
            hideProgress()
            showMessage(e.message!!)
        } catch (e: Throwable) {
            hideProgress()
            showMessage(e.message!!)
        }
    }

    private fun getRequest(user: User): Request {
        val data = HashMap<String, String>()
        data[RequestData.FROM_UID] = FireAuth.getCurrentUserId()!!
        data[RequestData.FROM_NAME] = FireAuth.getCurrentUser()!!.displayName!!
        data[RequestData.FROM_EMAIL] = FireAuth.getCurrentUser()!!.email!!
        data[RequestData.TO_UID] = user.uid
        data[RequestData.TO_NAME] = user.name
        data[RequestData.TO_EMAIL] = user.email

        val request = Request()
        request.to = user.tokens!!
        request.data = data

        return request
    }

    fun reloadData() {
        isDataLoading.value?.let { isLoading ->
            if (isLoading) return
            loadAllPeople()
        }
    }

    private fun showProgress() {
        _showProgress.postValue(true)
    }

    private fun hideProgress() {
        _showProgress.postValue(false)
    }

    private fun showMessage(message: String) {
        _showMessage.postValue(Event(message))
    }
}