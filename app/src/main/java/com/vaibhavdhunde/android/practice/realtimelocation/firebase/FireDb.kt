package com.vaibhavdhunde.android.practice.realtimelocation.firebase

import android.location.Location
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.vaibhavdhunde.android.practice.realtimelocation.model.RequestData
import com.vaibhavdhunde.android.practice.realtimelocation.model.User
import com.vaibhavdhunde.android.practice.realtimelocation.model.UserField
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object FireDb {

    private object Collection {
        const val USERS = "users"
        const val REQUESTS = "requests"
        const val ACCEPT_LIST = "acceptList"
        const val LOCATION = "location"
    }

    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val usersRef
        get() = db.getReference(Collection.USERS)

    val locationRef
        get() = db.getReference(Collection.LOCATION)

    val currentUserRef
        get() = db.getReference(Collection.USERS).child(
            FireAuth.getCurrentUserId() ?: throw NullPointerException("UID is null")
        )

    val currentUserRequestsRef
        get() = currentUserRef.child(Collection.REQUESTS)

    fun initUserIfFirstTime(onComplete: (User?) -> Unit) {
        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(e: DatabaseError) {
                Timber.e("Failed to fetch current user due to: ${e.message}")
                onComplete(null)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    onComplete(user)
                } else {
                    createUser(onComplete)
                }
            }
        })
    }

    private fun createUser(onComplete: (User?) -> Unit) {
        val uid = FireAuth.getCurrentUserId()!!
        val name = FireAuth.getCurrentUser()!!.displayName!!
        val email = FireAuth.getCurrentUser()!!.email!!
        val user = User(uid, name, email)
        currentUserRef.setValue(user).addOnSuccessListener {
            onComplete(user)
        }.addOnFailureListener { exception ->
            Timber.e("Failed to create user due to: ${exception.message}")
            onComplete(null)
        }
    }

    suspend fun updateToken(): Status {
        return try {
            val token = getRegistrationToken() ?: Status.FAILURE
            currentUserRef.updateChildren(mapOf(UserField.TOKENS to token)).await()
            Status.SUCCESS
        } catch (e: FirebaseException) {
            Timber.e("Failed to update registration token due to: ${e.message}")
            Status.FAILURE
        }
    }

    fun updateNewToken(token: String) {
        try {
            currentUserRef.updateChildren(mapOf(UserField.TOKENS to token))
        } catch (e: FirebaseException) {
            Timber.e("Failed to update new registration token due to: ${e.message}")
        }
    }

    private suspend fun getRegistrationToken(): String? {
        return try {
            val result = FirebaseInstanceId.getInstance().instanceId.await()
            result.token
        } catch (e: FirebaseException) {
            Timber.e("Failed to fetch registration token due to: ${e.message}")
            null
        }
    }

    fun checkRequestAlreadySent(uid: String, onComplete: (RequestStatus) -> Unit) {
        usersRef.child(uid).child(Collection.REQUESTS)
            .orderByKey()
            .equalTo(FireAuth.getCurrentUserId())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) {
                    Timber.e("Failed to check user friend request due to: ${e.message}")
                    onComplete(RequestStatus.FAILURE)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        onComplete(RequestStatus.NOT_SENT)
                    } else {
                        onComplete(RequestStatus.SENT)
                    }
                }
            })
    }

    fun checkUserAlreadyFriend(uid: String, onComplete: (FriendStatus) -> Unit) {
        currentUserRef.child(Collection.ACCEPT_LIST)
            .orderByKey()
            .equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) {
                    Timber.e("Failed to check user already friend due to: ${e.message}")
                    onComplete(FriendStatus.FAILURE)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        onComplete(FriendStatus.NOT_A_FRIEND)
                    } else {
                        onComplete(FriendStatus.ALREADY_A_FRIEND)
                    }
                }
            })
    }

    @Suppress("SpellCheckingInspection")
    fun unfriend(uid: String, onComplete: (Status) -> Unit) {
        try {
            // remove friend from current user's acceptList
            currentUserRef.child(Collection.ACCEPT_LIST).child(uid).removeValue()
            // remove current user from friend's acceptList
            usersRef.child(uid).child(Collection.ACCEPT_LIST).child(FireAuth.getCurrentUserId()!!)
                .removeValue()
            onComplete(Status.SUCCESS)
        } catch (e: FirebaseException) {
            Timber.e("Failed to delete friend due to: ${e.message}")
            onComplete(Status.FAILURE)
        }
    }

    fun addRequest(data: Map<String, String>) {
        try {
            val uid = data.getValue(RequestData.FROM_UID)
            val name = data.getValue(RequestData.FROM_NAME)
            val email = data.getValue(RequestData.FROM_EMAIL)
            val user = User(uid, name, email)
            currentUserRequestsRef.child(uid).setValue(user)
        } catch (e: FirebaseException) {
            Timber.e("Failed to add request due to: ${e.message}")
        }
    }

    fun deleteRequest(uid: String) {
        try {
            // remove request from current user's list
            currentUserRequestsRef.child(uid).removeValue()
            // remove request from friend's list
            usersRef.child(uid).child(Collection.REQUESTS).child(FireAuth.getCurrentUserId()!!)
                .removeValue()
        } catch (e: FirebaseException) {
            Timber.e("Failed to delete request due to: ${e.message}")
        }
    }

    suspend fun addToAcceptList(user: User): Status {
        try {
            // to user to current user's accept list
            currentUserRef.child(Collection.ACCEPT_LIST).child(user.uid).setValue(user).await()

            // to another user's accept list
            val uid = FireAuth.getCurrentUserId()!!
            val name = FireAuth.getCurrentUser()!!.displayName!!
            val email = FireAuth.getCurrentUser()!!.email!!
            val currentUser = User(uid, name, email)
            usersRef.child(user.uid).child(Collection.ACCEPT_LIST)
                .child(uid).setValue(currentUser).await()

            return Status.SUCCESS
        } catch (e: FirebaseException) {
            Timber.e("Failed to add user to accept list due to: ${e.message}")
            return Status.FAILURE
        }
    }

    fun updateLocation(uid: String, location: Location) {
        try {
            locationRef.child(uid).setValue(location)
        } catch (e: FirebaseException) {
            Timber.e("Failed to update user location due to: ${e.message}")
        }
    }

    fun getAllFriends(onComplete: (List<User>?) -> Unit) {
        try {
            currentUserRef.child(Collection.ACCEPT_LIST)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(e: DatabaseError) {
                        Timber.e("Failed to fetch friends due to: ${e.message}")
                        onComplete(null)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val users = mutableListOf<User>()
                        for (user in dataSnapshot.children) {
                            users.add(user.getValue(User::class.java)!!)
                        }
                        onComplete(users)
                    }
                })
        } catch (e: FirebaseException) {
            Timber.e("Failed to fetch friends due to: ${e.message}")
            onComplete(null)
        }
    }

    fun getAllFriendRequests(onComplete: (List<User>?) -> Unit) {
        try {
            currentUserRequestsRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) {
                    Timber.e("Failed to fetch friend requests due to: ${e.message}")
                    onComplete(null)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (user in dataSnapshot.children) {
                        users.add(user.getValue(User::class.java)!!)
                    }
                    onComplete(users)
                }
            })
        } catch (e: FirebaseException) {
            Timber.e("Failed to fetch friend requests due to: ${e.message}")
            onComplete(null)
        }
    }

    fun getAllPeople(onComplete: (List<User>?) -> Unit) {
        try {
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) {
                    Timber.e("Failed to fetch people due to: ${e.message}")
                    onComplete(null)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (user in dataSnapshot.children) {
                        users.add(user.getValue(User::class.java)!!)
                    }
                    onComplete(users)
                }
            })
        } catch (e: FirebaseException) {
            Timber.e("Failed to fetch people due to: ${e.message}")
            onComplete(null)
        }
    }
}